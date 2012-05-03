package com.findwise.thesis.qlustr.processing;

public abstract class MutatingStage implements PipelineStage {
	@Override
	public final TextData[] process(TextData... input) {
		for (int i = 0; i < input.length; i++) {
			input[i].ensureBackup();
			mutate(input[i]);
		}
		return input;
	}
	
	public abstract void mutate(TextData input);
}