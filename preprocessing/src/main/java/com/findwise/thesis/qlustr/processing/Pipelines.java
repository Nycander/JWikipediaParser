package com.findwise.thesis.qlustr.processing;

import com.findwise.thesis.qlustr.processing.steps.*;

/**
 * @author Martin Nycander (martin.nycander@gmail.com)
 */
public class Pipelines {
	public static Pipeline newTermFrequencyPipeline() {
		final Pipeline pipeline = new Pipeline();
		
		pipeline.addStep(new StanfordTokenizer());
		pipeline.addStep(new LowerCaser());
		pipeline.addStep(new PureSymbolsRemover());
		pipeline.addStep(new StopWordRemover());
		pipeline.addStep(new Stemmer());
		pipeline.addStep(new TermCounter());
		
		return pipeline;
	}
	
	public static Pipeline newTokenizer() {
		return new Pipeline(new StanfordTokenizer(), new PureSymbolsRemover());
	}
	
	public static Pipeline newReduceTermspacer() {
		final Pipeline pipeline = new Pipeline();
		
		pipeline.addStep(new LowerCaser());
		pipeline.addStep(new StopWordRemover());
		pipeline.addStep(new PureSymbolsRemover());
		pipeline.addStep(new Stemmer());
		
		return pipeline;
	}
	
	public static Pipeline newTfidfPipeline() {
		final Pipeline cleaner = new Pipeline(newTokenizer(),
				newReduceTermspacer());
		return new Pipeline(new TfidfCalculator(cleaner));
	}
	
	public static Pipeline newSentenceSplitter() {
		return new Pipeline(new SentenceSplitter());
	}
	
	public static Pipeline newCleaner() {
		return new Pipeline(new LowerCaser());
	}
}
