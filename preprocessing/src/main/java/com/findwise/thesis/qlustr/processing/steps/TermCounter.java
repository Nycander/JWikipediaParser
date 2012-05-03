package com.findwise.thesis.qlustr.processing.steps;

import java.util.*;

import com.findwise.thesis.qlustr.processing.*;

/**
 * The {@link TermCounter} can count the occurrences of the different terms in a
 * text.
 * 
 * @author Martin Nycander (martin.nycander@gmail.com)
 */
public class TermCounter extends MergingStage {
	@Override
	public TextData[] process(TextData... input) {
		Map<String, TextData> result = new HashMap<String, TextData>();
		for (TextData in : input) {
			TextData existing = result.get(in.getText());
			int count = 0;
			
			if (existing == null) {
				existing = in;
			} else {
				count = (Integer) existing.getField("count");
				existing = merge(existing, in);
			}
			
			result.put(in.getText(), existing);

			Integer incrementalCount = (Integer) in.getField("count");
			if (incrementalCount == null)
				incrementalCount = 1;
			
			existing.putField("count", count + incrementalCount);
		}
		
		return result.values().toArray(new TextData[0]);
	}
}
