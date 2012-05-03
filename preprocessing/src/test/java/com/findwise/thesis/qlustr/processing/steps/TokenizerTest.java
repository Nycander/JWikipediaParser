package com.findwise.thesis.qlustr.processing.steps;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Before;
import org.junit.Test;

import com.findwise.thesis.qlustr.processing.TextData;

public class TokenizerTest {
	private Tokenizer tokenizer;
	
	@Before
	public void setUp() throws Exception {
		tokenizer = new Tokenizer();
	}
	
	@Test
	public void testProcess() {
		TextData[] actual = tokenizer
				.process(input("one two two	three three three"));
		
		assertArrayEquals(
				input("one", "two", "two", "three", "three", "three"), actual);
	}
	
	@Test
	public void testProcess_with_punctuations() {
		TextData[] actual = tokenizer
				.process(input("one, two! two.... three: three!?! (three)"));
		
		assertArrayEquals(
				input("one", "two", "two", "three", "three", "three"), actual);
	}
	
	@Test
	public void testProcess_with_different_whitspace() {
		TextData[] actual = tokenizer
				.process(input("one  two	two\nthree\r\nthree\nthree"));
		
		assertArrayEquals(
				input("one", "two", "two", "three", "three", "three"), actual);
		
	}
	
	public TextData[] input(String... strs) {
		TextData[] res = new TextData[strs.length];
		for (int i = 0; i < strs.length; i++)
			res[i] = new TextData(strs[i]);
		return res;
	}
}
