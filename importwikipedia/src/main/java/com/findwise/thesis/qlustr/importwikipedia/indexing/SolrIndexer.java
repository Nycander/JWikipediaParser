package com.findwise.thesis.qlustr.importwikipedia.indexing;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.apache.solr.client.solrj.*;
import org.apache.solr.client.solrj.response.*;
import org.apache.solr.client.solrj.util.*;
import org.apache.solr.common.*;

import com.findwise.thesis.qlustr.importwikipedia.*;
import com.findwise.thesis.qlustr.processing.*;
import com.findwise.thesis.qlustr.processing.steps.*;

/**
 * @author Martin Nycander (martin.nycander@gmail.com)
 */
public class SolrIndexer extends BaseIndexer implements Runnable, Index {
	private static final int batchSize = 500;

	private final SolrServer server;
	private final Set<Integer> indexedHashcodeIDs;
	private final Pipeline preprocessing;
	private final List<TextData> documentSnapshot;

	private List<SolrInputDocument> documentBatch;

	public SolrIndexer(SolrServer server) {
		this.server = server;
		this.indexedHashcodeIDs = new HashSet<Integer>();
		
		documentBatch = new ArrayList<SolrInputDocument>(batchSize);
		documentSnapshot = new ArrayList<TextData>();
		preprocessing = new Pipeline();
		preprocessing.addStep(new WikitextParser(), documentSnapshot);
		preprocessing.addStep(new LowerCaser());
		preprocessing.addStep(new Tokenizer());
		preprocessing.addStep(new StopWordRemover());
		preprocessing.addStep(new TermCounter());
	}
	
	@Override
	public void clearIndex() {
		try {
			server.deleteByQuery("*:*");
			server.commit();
		} catch (SolrServerException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean contains(String id) {
		return indexedHashcodeIDs.contains(id.hashCode());
	}
	
	@Override
	public void readIndex() {
		try {
			SolrQuery query = new SolrQuery("*:*");
			query.setFields("id");
			query.setRows(Integer.MAX_VALUE);
			QueryResponse response = server.query(query);
			for (SolrDocument doc : response.getResults()) {
				indexedHashcodeIDs.add(doc.getFieldValue("id").toString()
						.hashCode());
			}
		} catch (SolrServerException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static Map<String, Synonym> synonyms = new HashMap<String, Synonym>();
	
	private void addSynonym(String id, String title) {
		Synonym synonym = synonyms.get(id);
		if (synonym == null) {
			synonym = new Synonym(id, title);
			synonyms.put(id, synonym);
		}
		else
			synonym.titles.add(title);
	}
	
	private List<String> flushSynonyms(String id) {
		Synonym synonym = synonyms.remove(id);
		if (synonym == null)
			return Collections.emptyList();
		
		return synonym.titles;
	}

	private class Synonym {
		public final String id;
		public final List<String> titles;
		
		private Synonym(String id, String... titles) {
			this.id = id;
			this.titles = new ArrayList<String>(titles.length);
			this.titles.addAll(Arrays.asList(titles));
		}
		
		@Override
		public String toString() {
			return id + "->" + titles.toString();
		}
	}

	@Override
	public void index(TextData take) {
		if (take instanceof RedirectData) {
			final RedirectData redirect = (RedirectData) take;

			final String id = String.valueOf(redirect.title.hashCode());
			SolrQuery query = new SolrQuery("id:\""+id+ "\"");
			try {
				final String alternateTitle = redirect.alternateTitle;

				SolrDocumentList result = server.query(query).getResults();
				if (result.size() > 0) {
					addAlternateTitle(result, alternateTitle);
				} else {
					addSynonym(id, alternateTitle);
				}
			} catch (SolrServerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			};
			
		} else {
			TextData[] processingResult = preprocessing.process(take);
			Map<String, Integer> termCounts = TextData.toMap(processingResult,
					"count");
			
			assert (documentSnapshot.size() == 1);
			TextData document = documentSnapshot.get(0);
			documentSnapshot.clear();
			
			List<String> terms = new ArrayList<String>(termCounts.size());
			List<Integer> counts = new ArrayList<Integer>(termCounts.size());
			for (Entry<String, Integer> e : termCounts.entrySet()) {
				terms.add(e.getKey());
				counts.add(e.getValue());
			}
			document.putField("terms", terms);
			document.putField("term_counts", counts);
			
			documentBatch.add(textDataToSolrInputDocument(document));
		}
		
		if (documentBatch.size() >= batchSize || !moreDocumentsExist()) {
			try {
				sendDocuments(documentBatch);
			} catch (SolrServerException e1) {
				throw new RuntimeException(e1);
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
		}
	}
	
	private void addAlternateTitle(SolrDocumentList result,
			String alternateTitle) throws SolrServerException, IOException {
		SolrDocument res = result.get(0);
		res.addField("title", alternateTitle);
		
		server.add(ClientUtils.toSolrInputDocument(res));
		server.commit();
	}
	
	@Override
	public void cleanIndex() {
		if (!documentBatch.isEmpty()) {
			try {
				sendDocuments(documentBatch);
			} catch (SolrServerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		optimize();
	}
	
	private SolrInputDocument textDataToSolrInputDocument(TextData text) {
		if (text == null)
			throw new NullPointerException("Document may not be null.");
		
		SolrInputDocument document = new SolrInputDocument();
		
		document.setField("text", text.getText());
		
		for (Map.Entry<String, Object> field : text.fields()) {
			document.setField(field.getKey(), field.getValue());
		}
		
		for (String altTitle : flushSynonyms(text.getField("id").toString())) {
			document.addField("title", altTitle);
		}

		return document;
	}

	protected void sendDocuments(List<SolrInputDocument> documentBatch)
			throws SolrServerException, IOException {
		server.add(documentBatch);
		server.commit();
		
		for (SolrInputDocument doc : documentBatch) {
			indexedHashcodeIDs.add(doc.getFieldValue("id").toString()
					.hashCode());
		}
		documentBatch.clear();
		
		System.out.println("Sent " + indexedHashcodeIDs.size()
				+ " documents.\t(There are " + countQueue()
				+ " documents in the pipe)");
	}
	
	private void optimize() {
		try {
			System.out.print(" Optimizing... ");
			server.optimize();
			System.out.println(" Done.");
		} catch (Exception e) {
			System.out.println(" Error: " + e.getMessage());
		}
	}
}
