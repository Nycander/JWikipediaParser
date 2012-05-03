package com.findwise.thesis.qlustr.processing.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.findwise.thesis.qlustr.processing.TextData;

public class TermCounterTest {
	private TermCounter termcalc;
	
	@Before
	public void setUp() throws Exception {
		termcalc = new TermCounter();
	}
	
	@Test
	public void testGetTermCounts_simple() {
		TextData[] output = termcalc.process(constructInput("three", "three",
				"one", "three"));
		
		assertEquals(2, output.length);
		
		Map<String, Integer> expected = new HashMap<String, Integer>();
		expected.put("three", 3);
		expected.put("one", 1);
		
		assertOutput(output, expected);
	}
	
	@Test
	public void testGetTermCounts_with_special_chars() {
		TextData[] output = termcalc.process(constructInput("årorna", "äro",
				"väldigt", "goda", "sa", "årorna"));
		
		Map<String, Integer> expected = new HashMap<String, Integer>();
		expected.put("årorna", 2);
		expected.put("äro", 1);
		expected.put("väldigt", 1);
		expected.put("goda", 1);
		expected.put("sa", 1);
		assertOutput(output, expected);
	}
	
	@Test
	public void testGetTermCounts_withExistingCounts() {
		TextData one = new TextData("1");
		one.putField("count", 3);
		
		TextData one2 = new TextData("1");
		one2.putField("count", 2);
		
		TextData one3 = new TextData("1");
		
		TextData[] actual = termcalc
				.process(new TextData[] { one, one2, one3 });
		
		assertEquals(6, actual[0].getField("count"));
	}
	
	private TextData[] constructInput(String... input) {
		List<TextData> in = new ArrayList<TextData>(input.length);
		for (String i : input)
			in.add(new TextData(i));
		return in.toArray(new TextData[0]);
	}
	
	private void assertOutput(TextData[] output, Map<String, Integer> expected) {
		for (TextData o : output) {
			String term = o.getText();
			if (!expected.containsKey(term))
				fail("'" + term + "' should not exist in output.");
			
			assertEquals("'" + term + "' has count=" + o.getField("count")
					+ ", expected was " + expected.get(term),
					expected.get(term), o.getField("count"));
		}
	}
}
