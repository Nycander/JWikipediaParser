package com.findwise.thesis.qlustr.importwikipedia;

import java.io.*;
import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.solr.client.solrj.impl.*;

import com.findwise.thesis.qlustr.importwikipedia.indexing.*;
import com.findwise.thesis.qlustr.importwikipedia.wikipediaparser.*;
import com.findwise.thesis.qlustr.processing.*;

public class ImportWikipedia {
	// Change this if you want to keep documents.
	private static boolean resetData = false;
	
	private static UncaughtExceptionHandler killSwitch = new UncaughtExceptionHandler() {
		public void uncaughtException(Thread t, Throwable e) {
			e.printStackTrace();
			System.exit(e.hashCode());
		}
	};

	public static void main(String[] args) throws Exception {
		String solrURL = "http://localhost:8080/solr/";
		String wikipediaDump = "/home/martin/exjobb/dev/dump.xml";
		// String wikipediaDump = "/home/co/kth/master/exjobb/dev/dump.xml";
		
		final PipeLine<TextData> pipeLine = new PipeLine<TextData>();
		
		BaseIndexer indexer = new SolrIndexer(
				new CommonsHttpSolrServer(solrURL));
		// indexer = new SysoutIndexer();
		indexer.setInputPipeline(pipeLine);
		if (resetData) {
			System.out.print("Clearing index... ");
			indexer.clearIndex();
			System.out.println("Done.");
		}
		
		System.out.print("Fetch list of documents in index... ");
		indexer.readIndex();
		System.out.println("Done.");
		
		WikipediaDumpReader wikipediaReader = new WikipediaDumpReader(new File(
				wikipediaDump), pipeLine, indexer);
		
		System.out.print("Starting thread to read wikipedia... ");
		Thread xmlReaderThread = new Thread(wikipediaReader);
		xmlReaderThread.setUncaughtExceptionHandler(killSwitch);
		xmlReaderThread.start();
		System.out.println(" Done.");
		
		System.out
				.println("Starting to send parsed documents to the indexer... ");
		indexer.run();
		wikipediaReader.setRunning(false);
		xmlReaderThread.interrupt();
		System.exit(0);
	}
}
