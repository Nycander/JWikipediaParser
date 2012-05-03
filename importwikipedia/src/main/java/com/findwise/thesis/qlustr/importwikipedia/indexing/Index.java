package com.findwise.thesis.qlustr.importwikipedia.indexing;

import com.findwise.thesis.qlustr.processing.*;


public interface Index {
	void readIndex();
	
	void clearIndex();
	
	void cleanIndex();
	
	void index(TextData take);

	boolean contains(String documentID);
}
