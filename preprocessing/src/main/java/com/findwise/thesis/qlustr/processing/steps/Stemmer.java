package com.findwise.thesis.qlustr.processing.steps;

import org.tartarus.snowball.*;
import org.tartarus.snowball.ext.*;

import com.findwise.thesis.qlustr.processing.*;

/**
 * @author Carl-Oscar Erneholm (co.erneholm@gmail.com)
 */
public class Stemmer extends MutatingStage {
	private SnowballProgram sp;
	
	public Stemmer() {
		sp = new SwedishStemmer();
	}
	
	@Override
	public void mutate(TextData textData) {
		sp.setCurrent(textData.getText());
		sp.stem();
		textData.setText(sp.getCurrent());
	}
}
