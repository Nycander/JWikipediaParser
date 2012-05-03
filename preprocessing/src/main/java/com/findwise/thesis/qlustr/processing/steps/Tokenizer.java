package com.findwise.thesis.qlustr.processing.steps;

import java.util.*;

import com.findwise.thesis.qlustr.processing.*;

/**
 * A pipeline step which splits up strings into tokens by predefined delimiter
 * characters.
 * 
 * @author Martin Nycander (martin.nycander@gmail.com)
 */
public class Tokenizer extends SplittingStage {
	private static final String DELIM = ".,! ()[]?:;*\r\n\t\"'#";
	
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
