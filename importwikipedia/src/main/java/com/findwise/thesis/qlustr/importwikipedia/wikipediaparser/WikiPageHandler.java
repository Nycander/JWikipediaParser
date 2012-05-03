package com.findwise.thesis.qlustr.importwikipedia.wikipediaparser;

import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import com.findwise.thesis.qlustr.importwikipedia.*;
import com.findwise.thesis.qlustr.importwikipedia.indexing.*;
import com.findwise.thesis.qlustr.processing.*;

class WikiPageHandler extends DefaultHandler {
	private static final Set<String> handledTags = new HashSet<String>(4);
	static {
		handledTags.add("title");
		handledTags.add("text");
		handledTags.add("page");
	}
	
	private StringBuilder characters = new StringBuilder();
	private TextData page;
	
	private final PipeLine<TextData> output;
	private final XMLStateMachine stateMachine;
	private final Index index;
	private boolean shouldSave;
	
	public WikiPageHandler(PipeLine<TextData> output,
			XMLStateMachine stateMachine, Index index) {
		this.output = output;
		this.stateMachine = stateMachine;
		this.index = index;
		this.page = new TextData();
		this.shouldSave = true;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (handledTags.contains(localName) && shouldSave)
			characters = new StringBuilder();
		else
			characters = null;
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (characters != null)
			characters.append(ch, start, length);
	}
	
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		try {
			if (!shouldSave)
				return;

			if (localName.equals("title")) {
				final String title = characters.toString().trim();

				page.putField("title", title);
				page.putField("id", String.valueOf(title.hashCode()));
				shouldSave = index.contains(title);
			} else if (localName.equals("text")) {
				final String text = characters.toString().trim();
				if (text.toUpperCase().startsWith("#OMDIRIGERING")) {
					handleRedirect(text, "#OMDIRIGERING");
				} else if (text.toUpperCase().startsWith("#REDIRECT")) {
					handleRedirect(text, "#REDIRECT");
				} else {
					page.setText(text);
				}
			} else if (localName.equals("page")) {
				tryToPutDocumentOnQueue(page);
			}
		} finally {
			// Return to base state
			if (localName.equals("page"))
				stateMachine.popState();
		}
	}
	
	/**
	 * @param text
	 * @param redirect
	 */
	private void handleRedirect(final String text, final String redirect) {
		int start = text.indexOf("[[") + 2;

		if (text.startsWith(redirect + ":"))
			start++;
		
		int end = text.indexOf("]]");
		int hashPos = text.indexOf("#", 1);
		if (0 < hashPos && hashPos < end)
			end = hashPos;
		
		String redirectsTo = text.substring(start, end);
		
		if (index.contains(redirectsTo)) {
			output.put(new RedirectData(page.getField("id").toString(),
					redirectsTo));
		}
		shouldSave = false;
	}
	
	private void tryToPutDocumentOnQueue(TextData document) {
		if (index.contains(document.getField("id").toString()))
			output.put(document);
	}
}