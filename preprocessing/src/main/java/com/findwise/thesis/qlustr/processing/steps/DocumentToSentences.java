package com.findwise.thesis.qlustr.processing.steps;

import java.io.*;
import java.util.*;

import com.findwise.thesis.qlustr.processing.*;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.process.*;
import edu.stanford.nlp.process.PTBTokenizer.PTBTokenizerFactory;
import edu.stanford.nlp.process.Tokenizer;

/**
 * Splitts a string into pieces seperated by . ! and ?. Should be used before a
 * tokenizer to achive the desired effect.
 * 
 * @author Carl-Oscar Erneholm (coer@kth.se)
 */
public class DocumentToSentences extends SplittingStage {
	@Override
	public TextData[] split(TextData... input) {
		List<TextData> output = new ArrayList<TextData>();
		for (TextData document : input) {
			StanfordTokenizer tokenizer = new StanfordTokenizer();
			input = tokenizer.process(input);

			WordToSentenceProcessor<Word> sentenceSplitter = new WordToSentenceProcessor<Word>();

			StringReader sr = new StringReader(document.getText());
			PTBTokenizerFactory<Word> tokenizerFactory = PTBTokenizer.PTBTokenizerFactory
					.newPTBTokenizerFactory(false);

			Tokenizer<Word> t = tokenizerFactory.getTokenizer(sr);
			List<Word> tokenize = t.tokenize();
			List<List<Word>> sentences = sentenceSplitter.process(tokenize);
			for (int i = 0; i < sentences.size(); i++) {
				StringBuffer sb = new StringBuffer();
				List<Word> list = sentences.get(i);
				for (int j = 0; j < list.size(); j++) {
					sb.append(PTBTokenizer.ptbToken2Text(list.get(j).word()));
					if (j + 1 < list.size())
						sb.append(" ");
				}
				
				TextData sentence = new TextData(sb.toString());
				output.add(sentence);
			}
		}

		return output.toArray(new TextData[0]);
	}
}
