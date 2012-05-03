package com.findwise.thesis.qlustr.processing.steps;

import java.util.*;

import com.findwise.thesis.qlustr.processing.*;

/**
 * @author Martin Nycander (martin.nycander@gmail.com)
 */
public class TfidfCalculator implements PipelineStage {
	private final PipelineStage preprocess;
	private final PipelineStage termCounter;
	private Map<String, Integer> termToIndex;
	
	public TfidfCalculator(PipelineStage preprocess) {
		this.preprocess = preprocess;
		this.termCounter = new TermCounter();
	}
	
	@Override
	public TextData[] process(TextData... input) {
		TextData[][] docsTerms = getDocumentTerms(input);
		Map<String, Double> documentFrequency = getDocumentFrequencies(docsTerms);
		
		termToIndex = new HashMap<String, Integer>();
		int index = 0;
		for (String term : documentFrequency.keySet()) {
			termToIndex.put(term, index++);
		}
		final int termCount = index;
		
		// Calculate term frequency / document frequency
		for (int i = 0; i < input.length; i++) {
			double[] tfidfs = new double[termCount];
			for (TextData term : docsTerms[i]) {
				double tfidf = ((Integer) term.getField("count")).doubleValue()
						/ documentFrequency.get(term.getText());
				tfidfs[termToIndex.get(term.getText())] = tfidf;
			}
			input[i].putField("tf-idf", tfidfs);
		}
		return input;
	}
	
	public Map<String, Integer> getTermToIndexMap() {
		return termToIndex;
	}

	private TextData[][] getDocumentTerms(TextData... input) {
		TextData[][] docsTerms = new TextData[input.length][];
		for (int i = 0; i < input.length; i++) {
			if (input[i].hasField("terms") && input[i].hasField("term_counts")) {
				docsTerms[i] = getDocumentTermsFromFields(input[i]);
			} else {
				docsTerms[i] = termCounter
						.process(preprocess.process(input[i]));
			}
		}
		return docsTerms;
	}
	
	@SuppressWarnings("unchecked")
	private TextData[] getDocumentTermsFromFields(TextData doc) {
		String[] terms = ((List<String>) doc.getField("terms"))
				.toArray(new String[0]);
		
		String[] termCounts = ((List<String>) doc.getField("term_counts"))
				.toArray(new String[0]);

		if (terms.length != termCounts.length)
			throw new IllegalArgumentException("Terms (size=" + terms.length
					+ ") and term counts (" + termCounts.length
					+ ") must be of same size.");

		TextData[] result = new TextData[terms.length];
		for(int i = 0; i < terms.length; i++) {
			result[i] = new TextData(terms[i]);
			result[i].putField("count", Integer.parseInt(termCounts[i]));
		}
		return result;
	}

	public static Map<String, Double> getDocumentFrequencies(
			TextData[][] docsTerms) {
		Map<String, Double> documentFrequency = new HashMap<String, Double>();
		for (int i = 0; i < docsTerms.length; i++) {
			for (TextData term : docsTerms[i]) {
				Double count = documentFrequency.get(term.getText());
				if (count == null)
					count = 0.0;
				count++;
				documentFrequency.put(term.getText(), count);
			}
		}
		return documentFrequency;
	}
}
