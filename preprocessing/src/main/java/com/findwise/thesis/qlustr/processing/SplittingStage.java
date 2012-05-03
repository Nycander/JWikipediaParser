package com.findwise.thesis.qlustr.processing;

public abstract class SplittingStage implements PipelineStage {
	@Override
	public final TextData[] process(TextData... input) {
		final TextData[] split = split(input);
		for (TextData d : split) {
			d.ensureBackup();
		}
		return split;
	}
	
	public abstract TextData[] split(TextData... input);
}
