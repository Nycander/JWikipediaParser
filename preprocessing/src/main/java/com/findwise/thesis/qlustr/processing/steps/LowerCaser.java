package com.findwise.thesis.qlustr.processing.steps;

import com.findwise.thesis.qlustr.processing.*;

/**
 * @author Martin Nycander (martin.nycander@gmail.com)
 */
public class LowerCaser extends MutatingStage {
	@Override
	public void mutate(TextData input) {
		input.setText(input.getText().toLowerCase());
	}
}
