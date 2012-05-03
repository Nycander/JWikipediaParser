package com.findwise.thesis.qlustr.processing.steps;

import java.util.*;

import com.findwise.thesis.qlustr.processing.*;

/**
 * Splits a string into pieces separated by . ! and ?. Should be used before a
 * tokenizer to achieve the desired effect.
 * 
 * @author Carl-Oscar Erneholm (coer@kth.se)
 */
public class SentenceSplitter extends SplittingStage {
	private static final String DELIM = ".!?";

	@Override
	public TextData[] split(TextData... input) {
		List<TextData> output = new ArrayList<TextData>();
		StringTokenizer tokenizer;
		for (TextData data : input) {
			tokenizer = new StringTokenizer(data.getText(), DELIM);
			while (tokenizer.hasMoreElements()) {
				String token = tokenizer.nextToken();
				TextData textData = new TextData(token);
				output.add(textData);
			}
		}
		return output.toArray(new TextData[0]);
	}
}
