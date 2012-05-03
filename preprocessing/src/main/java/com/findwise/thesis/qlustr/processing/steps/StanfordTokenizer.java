package com.findwise.thesis.qlustr.processing.steps;

import java.io.*;
import java.util.*;

import com.findwise.thesis.qlustr.processing.*;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.process.*;
import edu.stanford.nlp.process.PTBTokenizer.PTBTokenizerFactory;
import edu.stanford.nlp.process.Tokenizer;

/**
 * A pipeline step which splits up strings into tokens using The Stanford nlp
 * library.
 * 
 * @author Carl-Oscar Erneholm (co.erneholm@gmail.com)
 */
public class StanfordTokenizer extends SplittingStage {
	@Override
	public TextData[] split(TextData... input) {
		List<TextData> output = new ArrayList<TextData>();
		for (TextData data : input) {
			StringReader sr = new StringReader(data.getText());

			PTBTokenizerFactory<Word> tokenizerFactory = PTBTokenizer.PTBTokenizerFactory
					.newPTBTokenizerFactory(false);
			Tokenizer<Word> tokenizer = tokenizerFactory.getTokenizer(sr);

			ArrayList<Word> tokens = new ArrayList<Word>(tokenizer.tokenize());
			for (Word token : tokens) {
				TextData e = new TextData(PTBTokenizer.ptbToken2Text(token
						.word()));
				output.add(e);
			}
		}
		return output.toArray(new TextData[0]);
	}
}
