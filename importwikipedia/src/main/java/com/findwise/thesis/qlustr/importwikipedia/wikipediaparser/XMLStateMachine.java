package com.findwise.thesis.qlustr.importwikipedia.wikipediaparser;

import org.xml.sax.helpers.DefaultHandler;

public interface XMLStateMachine {
	void pushState(DefaultHandler state);
	
	void popState();
}
