package com.findwise.thesis.qlustr.processing;

import java.util.*;

public abstract class FilteringStage implements PipelineStage {

	public final TextData[] process(TextData... input) {
		List<TextData> result = new ArrayList<TextData>(input.length);
		for (int i = 0; i < input.length; i++) {
			if (shouldKeep(input[i]))
				result.add(input[i]);
		}
		return result.toArray(new TextData[result.size()]);
	}
	
	public abstract boolean shouldKeep(TextData textData);
}
