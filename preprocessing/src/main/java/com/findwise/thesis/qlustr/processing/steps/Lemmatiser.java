package com.findwise.thesis.qlustr.processing.steps;

import java.io.*;
import java.util.*;

import se.findwise.lemmatisation.domain.*;
import se.findwise.lemmatisation.exception.*;
import se.findwise.lemmatisation.model.*;
import se.findwise.lemmatisation.svc.*;

import com.findwise.thesis.qlustr.processing.*;

public class Lemmatiser extends MutatingStage {
	private static LemmatisationSvcImpl lemmatiser;
	private final Condition condition;
	
	public Lemmatiser(Condition condition) {
		this.condition = condition;
	}

	public Lemmatiser() {
		this(new Condition() {
			@Override
			public boolean test(TextData data) {
				return true;
			}
		});
	}

	public static void makeSureLemmatiserIsInitialized() {
		if (lemmatiser == null) // Lazy init
			lemmatiser = buildLemmatiser();
	}
	
	private static LemmatisationSvcImpl buildLemmatiser() {
		InputStream inputStream = Lemmatiser.class.getClassLoader()
				.getResourceAsStream("lemmas.txt");
		try {
			File lemmaDBFile = new File("lemma.txt");
			if (!lemmaDBFile.exists()) {
				FileOutputStream out = new FileOutputStream(lemmaDBFile);
				byte[] buffer = new byte[4096];
				int n = 0;
				while (-1 != (n = inputStream.read(buffer))) {
					out.write(buffer, 0, n);
				}
				out.close();
			}

			List<FilesystemDictionary> fsdList = new LinkedList<FilesystemDictionary>();
			fsdList.add(new FilesystemDictionary(lemmaDBFile.getPath(), "swe"));

			final LemmatisationSvcImpl lemmatiser = new LemmatisationSvcImpl();
			lemmatiser.setFilesystemDictionaries(fsdList);
			lemmatiser.setDefaultDictionary("swe");
			try {
				lemmatiser.init();
			} catch (InitializationException e) {
				throw new RuntimeException(e);
			}
			return lemmatiser;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void mutate(TextData input) {
		if (condition.test(input)) {
			input.setText(lemmatise(input.getText()));
		}
	}
	
	public static String lemmatise(final String term) {
		makeSureLemmatiserIsInitialized();

		List<LemmatisedObject> lemmas = lemmatiser.getParadigmsObject(term)
				.getList();
		
		// Word missing from dictionary?
		if (lemmas == null) {
			return term;
		}
		
		// Already in lemma word?
		for (int j = 0; j < lemmas.size(); j++) {
			String lemma = lemmas.get(j).getLemma();
			if (lemma.equals(term)) {
				// Do nothing, we already in lemma form
				return term;
			}
		}
		
		return lemmas.get(0).getLemma().toLowerCase();
	}
}
