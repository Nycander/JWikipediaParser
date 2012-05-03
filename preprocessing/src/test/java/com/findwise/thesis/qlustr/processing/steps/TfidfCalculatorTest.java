package com.findwise.thesis.qlustr.processing.steps;

import static org.junit.Assert.*;

import java.util.*;
import java.util.Map.Entry;

import org.junit.*;

import com.findwise.thesis.qlustr.processing.*;

public class TfidfCalculatorTest {
	private static final double delta = 0.000000001;
	
	private TfidfCalculator tfidfCalculator;
	
	@Before
	public void setUp() throws Exception {
		tfidfCalculator = new TfidfCalculator(Pipelines.newCleaner());
	}
	
	@SuppressWarnings("unchecked")
	public void testProcess() {
		TextData[] in = input("zero zero zero zero zero one one",
				"zero two two three three four",
				"zero three three three three three three four five five five five five one");
		TextData[] actual = tfidfCalculator.process(in);
		
		for (int i = 0; i < actual.length; i++)
			assertEquals(in[i].getText(), actual[i].getText());
		
		Map<String, Double> expectedTfidf = new HashMap<String, Double>();
		expectedTfidf.put("zero", 5.0 / 3.0);
		expectedTfidf.put("one", 1.0);
		expectedTfidf.put("two", 0.0);
		expectedTfidf.put("three", 0.0);
		expectedTfidf.put("four", 0.0);
		expectedTfidf.put("five", 0.0);
		double[] expected = new double[] { 5.0 / 3.0, 1.0, 0.0, 0.0, 0.0, 0.0 };
		assertArrayEquals(expected, (double[]) actual[0].getField("tf-idf"),
				delta);
		expectedTfidf.clear();
		expectedTfidf.put("zero", 1.0 / 3.0);
		expectedTfidf.put("one", 0.0);
		expectedTfidf.put("two", 2.0);
		expectedTfidf.put("three", 1.0);
		expectedTfidf.put("four", 0.5);
		expectedTfidf.put("five", 0.0);
		assertTfidfs(expectedTfidf,
				(Map<String, Double>) actual[1].getField("tf-idf"));
		
		expectedTfidf.clear();
		expectedTfidf.put("zero", 1.0 / 3.0);
		expectedTfidf.put("one", 0.5);
		expectedTfidf.put("two", 0.0);
		expectedTfidf.put("three", 3.0);
		expectedTfidf.put("four", 0.5);
		expectedTfidf.put("five", 5.0);
		assertTfidfs(expectedTfidf,
				(Map<String, Double>) actual[2].getField("tf-idf"));
	}
	
	private void assertTfidfs(Map<String, Double> expectedTfidf,
			Map<String, Double> actualTfidf) {
		for (Entry<String, Double> actual : actualTfidf.entrySet()) {
			assertTrue(expectedTfidf.containsKey(actual.getKey()));
			assertEquals("For '" + actual.getKey() + "'",
					expectedTfidf.get(actual.getKey()).doubleValue(), actual
							.getValue().doubleValue(), delta);
		}
	}
	
	@Test
	public void success() {
		
	}

	public TextData[] input(String... strs) {
		TextData[] res = new TextData[strs.length];
		for (int i = 0; i < strs.length; i++)
			res[i] = new TextData(strs[i]);
		return res;
	}
}
