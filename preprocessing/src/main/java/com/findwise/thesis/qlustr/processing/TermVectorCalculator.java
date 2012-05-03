package com.findwise.thesis.qlustr.processing;

import java.util.Map;

/**
 * @author Martin Nycander (martin.nycander@gmail.com)
 */
public interface TermVectorCalculator<DocumentID> {
	void setCorpusTerms(Map<String, Integer> corpusTerms);
	
	void setTermCounts(Map<DocumentID, Map<String, Integer>> termCounts);
	
	double calculateTermVector(DocumentID doc, String term);
}
