package com.findwise.thesis.qlustr.importwikipedia.wikipediaparser;

import java.io.*;
import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import com.findwise.thesis.qlustr.importwikipedia.*;
import com.findwise.thesis.qlustr.importwikipedia.indexing.*;
import com.findwise.thesis.qlustr.processing.*;

public class WikipediaDumpReader implements Runnable, XMLStateMachine, Index {
	private final PipeLine<TextData> output;
	private final XMLReader xmlReader;
	private final File wikipediaDump;
	private final MainDocumentHandler mainDocumentHandler;
	private final Stack<DefaultHandler> states = new Stack<DefaultHandler>();
	private final Index index;
	
	private boolean running = true;
	
	public WikipediaDumpReader(final File inputXml,
			final PipeLine<TextData> pipeLine, final Index index)
			throws SAXException {
		this.wikipediaDump = inputXml;
		this.index = index;
		
		this.xmlReader = XMLReaderFactory.createXMLReader();
		this.output = pipeLine;
		this.mainDocumentHandler = new MainDocumentHandler();
	}
	
	@Override
	public boolean contains(String title) {
		boolean exists = index.contains(String.valueOf((title.hashCode())));
		boolean isMetaPage = title.startsWith("Wikipedia:");
		isMetaPage |= title.startsWith("Mall:");
		isMetaPage |= title.startsWith("Kategori:");
		
		return !exists && !isMetaPage;
	}
	
	public void pushState(DefaultHandler state) {
		states.push(state);
		xmlReader.setContentHandler(state);
	}
	
	public void popState() {
		states.pop();
		xmlReader.setContentHandler(states.peek());
	}
	
	public void run() {
		pushState(mainDocumentHandler);
		
		try {
			xmlReader.parse(wikipediaDump.toURI().toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	private class MainDocumentHandler extends DefaultHandler {
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if (!isRunning())
				throw new SAXException("Stopped parsing.");
			
			if (localName.equals("page")) {
				pushState(new WikiPageHandler(output, WikipediaDumpReader.this,
						WikipediaDumpReader.this));
			}
		}
		
		@Override
		public void endDocument() throws SAXException {
			output.setDone();
		}
	}
	
	@Override
	public void readIndex() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void clearIndex() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void cleanIndex() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void index(TextData take) {
		throw new UnsupportedOperationException();
	}
}
