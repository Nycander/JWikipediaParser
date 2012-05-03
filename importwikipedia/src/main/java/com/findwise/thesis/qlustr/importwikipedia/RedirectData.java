package com.findwise.thesis.qlustr.importwikipedia;

import com.findwise.thesis.qlustr.processing.*;

public class RedirectData extends TextData {
	public final String title;
	public final String alternateTitle;

	public RedirectData(String title, String alternateTitle) {
		super();
		this.title = title;
		this.alternateTitle = alternateTitle;
	}
}
