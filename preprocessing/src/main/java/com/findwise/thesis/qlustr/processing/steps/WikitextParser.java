package com.findwise.thesis.qlustr.processing.steps;

import com.findwise.thesis.qlustr.processing.*;

import edu.jhu.nlp.wikipedia.*;

/**
 * Adds the field "categories" to each term and strips any wikitext from the
 * text.
 * 
 * @author Martin Nycander (martin.nycander@gmail.com)
 */
public class WikitextParser extends MutatingStage {
	@Override
	public void mutate(TextData input) {
		WikiTextParser parser = new WikiTextParser(input.getText());
		
		input.putField("categories", parser.getCategories());
		try {
			input.setText(parser.getPlainText().trim());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
