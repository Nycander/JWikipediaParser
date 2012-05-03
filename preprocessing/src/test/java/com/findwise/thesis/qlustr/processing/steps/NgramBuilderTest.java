package com.findwise.thesis.qlustr.processing.steps;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

import com.findwise.thesis.qlustr.processing.*;

public class NgramBuilderTest {
	private NgramBuilder biGrams;
	private NgramBuilder triGrams;
	private NgramBuilder quadGrams;

	private NgramBuilder manyGrams;

	private TextData[] ins;

	@Before
	public void setUp() throws Exception {
		ins = new TextData[] { new TextData("a"), new TextData("test"),
				new TextData("string") };

		biGrams = new NgramBuilder(2);
		triGrams = new NgramBuilder(3);
		quadGrams = new NgramBuilder(4);
		
		manyGrams = new NgramBuilder(1, 2, 3);
	}

	@Test
	public void testmergeTextDatas() {
		String actual = NgramBuilder.mergeTextDatas(0, ins.length, ins);
		assertEquals("a test string", actual);
		
		actual = NgramBuilder.mergeTextDatas(0, 1, ins);
		assertEquals("a", actual);
		
		actual = NgramBuilder.mergeTextDatas(0, 0, ins);
		assertEquals("", actual);
	}
	
	@Test
	public void testBigrams() {
		TextData[] bigrams = biGrams.split(ins);
		TextData[] expected = new TextData[] { get("a test", 2),
				get("test string", 2) };
		assertArrayEquals(expected, bigrams);
	}
	
	@Test
	public void testTrigrams() {
		TextData[] actual = triGrams.split(ins);
		TextData[] expected = new TextData[] { get("a test string", 3) };
		assertArrayEquals(expected, actual);
	}

	@Test
	public void testQuadgrams() {
		TextData[] actual = quadGrams.split(ins);
		TextData[] expected = new TextData[] {};
		assertArrayEquals(expected, actual);
	}
	
	@Test
	public void testManygrams() {
		TextData[] actual = manyGrams.split(ins);
		TextData[] expected = new TextData[] { get("a", 1), get("test", 1),
				get("string", 1), get("a test", 2), get("test string", 2),
				get("a test string", 3) };
		assertArrayEquals(expected, actual);
	}
	
	@SuppressWarnings("serial")
	private TextData get(String str, final int n) {
		return new TextData(str, new HashMap<String, Object>() {
			{
				this.put("n", n);
			}
		});
	}
}
