package com.findwise.thesis.qlustr.processing.steps;

import com.findwise.thesis.qlustr.processing.*;

/**
 * Removes tokens which are purely made up of symbols.
 * 
 * @author Martin Nycander (martin.nycander@gmail.com)
 */
public class PureSymbolsRemover extends FilteringStage {
	@Override
	public boolean shouldKeep(TextData data) {
		return !data.getText().matches("^[^a-zA-ZÅÄÖåäö0-9]+$");
	}
}
