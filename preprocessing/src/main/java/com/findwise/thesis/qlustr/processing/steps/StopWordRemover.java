package com.findwise.thesis.qlustr.processing.steps;

import java.util.*;

import com.findwise.thesis.qlustr.processing.*;

/**
 * @author Martin Nycander (martin.nycander@gmail.com)
 */
public class StopWordRemover extends FilteringStage {
	private static final Set<String> stopWords = new HashSet<String>();
	
	static {
		stopWords.addAll(Arrays.asList("och", "i", "av", "en", "som", "att",
				"till", "den", "på", "för", "med", "är", "det", "de", "ett",
				"om", "har", "hade", "vid", "var", "och", "även", "kan",
				"kunde", "hos", "flera", "han", "hon", "honom", "hennes",
				"henne", "hans", "jag", "sig", "du", "då", "ej", "vi", "sedan",
				"vilkas", "era", "ert", "så", "sådana", "vilken", "samma",
				"deras", "oss", "från", "under", "efter", "inte", "men", "man",
				"eller", "sin", "sina", "sitt", "mot", "blev", "över", "dess",
				"dessa", "detta", "finns", "mellan", "också", "när", "bland",
				"genom", "där", "skulle", "dock", "fick", "samt", "inom",
				"denna", "olika", "ut", "än", "vilket", "annat", "var", "vara",
				"senare", "mycket", "s", "annat", "vilket", "ha", "in", "alla",
				"många", "utan", "stora", "upp", "-", "–", "enda", "flest",
				"bli", "blir", "blivit", "medan", "medans", "måste", "vad",
				"vill"));
	}
	
	@Override
	public boolean shouldKeep(TextData textData) {
		return !stopWords.contains(textData.getText());
	}
}
