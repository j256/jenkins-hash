package com.j256.jenkinshash;

import static org.junit.Assert.*;

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
					2138388378L),

	};

	@Test
	public void testStuff() {
		for (TestPattern pattern : testPatterns) {
			long value = JenkinsHash.hash(pattern.bytes);
			assertEquals(pattern.value, value);
		}
	}

	private static class TestPattern {
		byte[] bytes;
		long value;

		public TestPattern(byte[] bytes, long value) {
			this.bytes = bytes;
			this.value = value;
		}

		public TestPattern(String str, long value) {
			this(str.getBytes(), value);
		}
	}
}
