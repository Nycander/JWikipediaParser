package com.findwise.thesis.qlustr.processing;

public abstract class MergingStage implements PipelineStage {
	
	protected TextData merge(TextData a, TextData b) {
		a.addOriginalText(b.getText());
		return a;
	}
}
