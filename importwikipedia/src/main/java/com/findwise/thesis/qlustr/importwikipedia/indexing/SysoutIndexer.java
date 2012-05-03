package com.findwise.thesis.qlustr.importwikipedia.indexing;

import com.findwise.thesis.qlustr.importwikipedia.*;
import com.findwise.thesis.qlustr.processing.*;
import com.findwise.thesis.qlustr.processing.steps.*;

/**
 * @author Martin Nycander (martin.nycander@gmail.com)
 */
public class SysoutIndexer extends BaseIndexer {
	
	@Override
	public void index(TextData take) {
		if (take instanceof RedirectData) {
			RedirectData redirect = (RedirectData) take;
			System.err.println(redirect.alternateTitle + " -> "
					+ redirect.title);
			return;
		}

		int indexOfNewline = take.getText().indexOf('\n');
		if (indexOfNewline == -1)
			indexOfNewline = take.getText().length();
		
		WikitextParser wikitextParser = new WikitextParser();
		wikitextParser.mutate(take);
		
		System.out.printf("%-20s : %-57s\n", take.getField("title").toString(),
				take.getField("categories"));
	}
	
	@Override
	public boolean contains(String documentID) {
		return false;
	}

	@Override
	public void readIndex() {
		// Do nothing, we have no index
	}
	
	@Override
	public void clearIndex() {
		// Do nothing, we have no index
	}
	
	@Override
	public void cleanIndex() {
		// Do nothing, we have no index
	}
}
