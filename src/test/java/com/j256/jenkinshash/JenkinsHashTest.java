package com.j256.jenkinshash;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class JenkinsHashTest {

	/**
	 * Test patterns verified again the C sources.
	 */
	private final TestPattern[] testPatterns = new TestPattern[] { //
			new TestPattern("", 3175731469L), //
			new TestPattern("a", 703514648L), //
			new TestPattern("The quick brown fox jumps over the lazy dog"
					+ "The quick brown fox jumps over the lazy dog" + "The quick brown fox jumps over the lazy dog"
					+ "The quick brown fox jumps over the lazy dog" + "The quick brown fox jumps over the lazy dog",
					2138388378L), //
			new TestPattern("hello", 3070638494L), //
			new TestPattern("wow", 627410295L), //
			new TestPattern(new byte[] { 0 }, 1843378377L), //
			new TestPattern(new byte[] { (byte) 255, (byte) 128, 64, 1 }, 3359486273L), //
			new TestPattern("this is 11c", 3384459500L), //
			new TestPattern("this is 12ch", 313177311L), //
			new TestPattern("this is >12ch", 2321813933L), //
			new TestPattern("this is much large than 12 characters", 2771373033L), //

			// patterns with initial values
			new TestPattern("hello", 3070638494L, 1535955511L), //
			new TestPattern("wow", 627410295L, 320141986L), //
			new TestPattern(new byte[] { 0 }, 1843378377L, 341630388L), //
			new TestPattern(new byte[] { (byte) 255, (byte) 128, 64, 1 }, 3359486273L, 2916354366L), //
			new TestPattern("this is 11c", 3384459500L, 1497460513L), //
			new TestPattern("this is 12ch", 313177311L, 1671722359L), //
			new TestPattern("this is >12ch", 2321813933L, 4197822112L), //
			new TestPattern("this is much large than 12 characters", 2771373033L, 1338302094L), //
	};

	@Test
	public void testStuff() {
		for (TestPattern pattern : testPatterns) {
			long value;
			if (pattern.initial == 0) {
				value = JenkinsHash.hash(pattern.bytes);
			} else {
				value = JenkinsHash.hash(pattern.bytes, pattern.initial);
			}
			assertEquals(pattern.value, value);
		}
	}

	private static class TestPattern {
		final byte[] bytes;
		final long initial;
		final long value;

		public TestPattern(byte[] bytes, long value) {
			this.bytes = bytes;
			this.initial = 0;
			this.value = value;
		}

		public TestPattern(byte[] bytes, long initial, long value) {
			this.bytes = bytes;
			this.initial = initial;
			this.value = value;
		}

		public TestPattern(String str, long value) {
			this(str.getBytes(), 0, value);
		}

		public TestPattern(String str, long initial, long value) {
			this(str.getBytes(), initial, value);
		}
	}
}
