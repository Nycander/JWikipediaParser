package com.findwise.thesis.qlustr.importwikipedia.indexing;

import com.findwise.thesis.qlustr.importwikipedia.*;
import com.findwise.thesis.qlustr.processing.*;

/**
 * @author Martin Nycander (martin.nycander@gmail.com)
 */
public abstract class BaseIndexer implements Index, Runnable {
	private PipeLine<TextData> inputPipeline;
	
	@Override
	public void run() {
		try {
			while (moreDocumentsExist()) {
				index(inputPipeline.take());
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			cleanIndex();
		}
	}
	
	protected boolean moreDocumentsExist() {
		return !inputPipeline.isDone();
	}
	
	protected int countQueue() {
		return inputPipeline.count();
	}

	public void setInputPipeline(PipeLine<TextData> pipeline) {
		inputPipeline = pipeline;
	}
}
