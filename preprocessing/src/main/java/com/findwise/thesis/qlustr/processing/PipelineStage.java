package com.findwise.thesis.qlustr.processing;

/**
 * @author Martin Nycander (martin.nycander@gmail.com)
 */
public interface PipelineStage {
	TextData[] process(final TextData... input);
}