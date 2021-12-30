package com.j256.jenkinshash;

/**
 * Hash algorithm by Bob Jenkins, 1996.
 * 
 * Use for hash table lookup, or anything where one collision in 2^^32 is acceptable. Do NOT use for cryptographic
 * purposes.
 *
 * You may use this code any way you wish, private, educational, or commercial. It's free. See:
 * https://burtleburtle.net/bob/hash/doobs.html
 *
 * Java port by Gray Watson http://256stuff.com/gray/
 */
public class JenkinsHash {

	// max value to limit it to 4 bytes
	private static final long MAX_VALUE = 0xFFFFFFFFL;
	// the golden ratio, an arbitrary value
	private static final long GOLDEN_RATIO_VALUE = 0x09E3779B9L;

	/**
	 * Hash a variable-length key into a 32-bit value. Every bit of the key affects every bit of the return value. Every
	 * 1-bit and 2-bit delta achieves avalanche. The best hash table sizes are powers of 2.
	 *
	 * @return 32-bit hash value for the buffer. It is a long because there is no unsigned int in Java.
	 * @param buffer
	 *            Byte array that we are hashing on.
	 * @param initialValue
	 *            Initial value of the hash if we are continuing from a previous run. 0 if none.
	 */
	public static long hash(byte[] buffer, long initialValue) {

		HashInfo info = new HashInfo(GOLDEN_RATIO_VALUE, GOLDEN_RATIO_VALUE, initialValue);

		// handle most of the key
		int pos = 0;
		int len;
		for (len = buffer.length; len >= 12; len -= 12) {
			info.a = add(info.a, fourByteToLong(buffer, pos));
			info.b = add(info.b, fourByteToLong(buffer, pos + 4));
			info.c = add(info.c, fourByteToLong(buffer, pos + 8));
			hashMix(info);
			pos += 12;
		}

		info.c += buffer.length;

		// all the case statements fall through to the next on purpose
		switch (len) {
			case 11:
				info.c = add(info.c, leftShift(byteToLong(buffer[pos + 10]), 24));
				// continue to next case
			case 10:
				info.c = add(info.c, leftShift(byteToLong(buffer[pos + 9]), 16));
				// continue to next case
			case 9:
				info.c = add(info.c, leftShift(byteToLong(buffer[pos + 8]), 8));
				// the first byte of c is reserved for the length
				// continue to next case
			case 8:
				info.b = add(info.b, leftShift(byteToLong(buffer[pos + 7]), 24));
				// continue to next case
			case 7:
				info.b = add(info.b, leftShift(byteToLong(buffer[pos + 6]), 16));
				// continue to next case
			case 6:
				info.b = add(info.b, leftShift(byteToLong(buffer[pos + 5]), 8));
				// continue to next case
			case 5:
				info.b = add(info.b, byteToLong(buffer[pos + 4]));
				// continue to next case
			case 4:
				info.a = add(info.a, leftShift(byteToLong(buffer[pos + 3]), 24));
				// continue to next case
			case 3:
				info.a = add(info.a, leftShift(byteToLong(buffer[pos + 2]), 16));
				// continue to next case
			case 2:
				info.a = add(info.a, leftShift(byteToLong(buffer[pos + 1]), 8));
				// continue to next case
			case 1:
				info.a = add(info.a, byteToLong(buffer[pos + 0]));
				// continue to next case
			case 0:
				// nothing left to add
				break;
		}
		hashMix(info);

		return info.c;
	}

	/**
	 * See {@link #hash(byte[], long)}
	 * 
	 * @return 32-bit hash value for the buffer. It is a long because there is no unsigned int in Java.
	 * @param buffer
	 *            Byte array that we are hashing on.
	 */
	public static long hash(byte[] buffer) {
		return hash(buffer, 0);
	}

	/**
	 * See {@link #hash(byte[], long)}
	 * 
	 * @return 32-bit hash value for the string. It is a long because there is no unsigned int in Java.
	 * @param str
	 *            String that we are calling {@link String#getBytes()} on and delegating to {@link #hash(byte[], long)}.
	 */
	public static long hash(String str) {
		return hash(str.getBytes(), 0);
	}

	/**
	 * Convert a byte into a long value without making it negative.
	 */
	private static long byteToLong(byte b) {
		long val = b & 0x7F;
		if ((b & 0x80) != 0) {
			val += 128;
		}
		return val;
	}

	/**
	 * Do addition and turn into 4 bytes.
	 */
	private static long add(long val, long add) {
		return (val + add) & MAX_VALUE;
	}

	/**
	 * Convert 4 bytes from the buffer at offset into a long value.
	 */
	private static long fourByteToLong(byte[] bytes, int offset) {
		return ((byteToLong(bytes[offset + 0]) << 0) //
				+ (byteToLong(bytes[offset + 1]) << 8) //
				+ (byteToLong(bytes[offset + 2]) << 16) //
				+ (byteToLong(bytes[offset + 3]) << 24));
	}

	/**
	 * Mix up the values in the hash function.
	 */
	private static void hashMix(HashInfo info) {
		info.a = subtract(info.a, info.b);
		info.a = subtract(info.a, info.c);
		info.a = xor(info.a, info.c >> 13);
		info.b = subtract(info.b, info.c);
		info.b = subtract(info.b, info.a);
		info.b = xor(info.b, leftShift(info.a, 8));
		info.c = subtract(info.c, info.a);
		info.c = subtract(info.c, info.b);
		info.c = xor(info.c, (info.b >> 13));
		info.a = subtract(info.a, info.b);
		info.a = subtract(info.a, info.c);
		info.a = xor(info.a, (info.c >> 12));
		info.b = subtract(info.b, info.c);
		info.b = subtract(info.b, info.a);
		info.b = xor(info.b, leftShift(info.a, 16));
		info.c = subtract(info.c, info.a);
		info.c = subtract(info.c, info.b);
		info.c = xor(info.c, (info.b >> 5));
		info.a = subtract(info.a, info.b);
		info.a = subtract(info.a, info.c);
		info.a = xor(info.a, (info.c >> 3));
		info.b = subtract(info.b, info.c);
		info.b = subtract(info.b, info.a);
		info.b = xor(info.b, leftShift(info.a, 10));
		info.c = subtract(info.c, info.a);
		info.c = subtract(info.c, info.b);
		info.c = xor(info.c, (info.b >> 15));
	}

	/**
	 * Left shift val by shift bits. Cut down to 4 bytes.
	 */
	private static long leftShift(long val, int shift) {
		return (val << shift) & MAX_VALUE;
	}

	/**
	 * Left shift val by shift bits and turn in 4 bytes.
	 */
	private static long xor(long val, long xor) {
		return (val ^ xor) & MAX_VALUE;
	}

	/**
	 * Do subtraction and turn into 4 bytes.
	 */
	private static long subtract(long val, long subtract) {
		return (val - subtract) & MAX_VALUE;
	}

	/**
	 * Internal variables used in the various calculations
	 */
	private static class HashInfo {
		long a;
		long b;
		long c;

		public HashInfo(long a, long b, long c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}
	}
}
