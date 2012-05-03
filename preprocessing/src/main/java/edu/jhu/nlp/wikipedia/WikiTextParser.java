package edu.jhu.nlp.wikipedia;

import java.util.*;
import java.util.regex.*;

/**
 * For internal use only -- Used by the {@link WikiPage} class. Can also be used
 * as a stand alone class to parse wiki formatted text.
 * 
 * @author Delip Rao
 * 
 */
public class WikiTextParser {
	private static final String CATEGORY_PREFIX = "Kategori:";
	
	private String wikiText = null;
	private Vector<String> pageCats = null;
	private Vector<String> pageLinks = null;
	private boolean redirect = false;
	private String redirectString = null;
	private static Pattern redirectPattern = Pattern
			.compile("#REDIRECT\\s+\\[\\[(.*?)\\]\\]");
	private boolean stub = false;
	private boolean disambiguation = false;
	private static Pattern stubPattern = Pattern.compile("\\-stub\\}\\}");
	private static Pattern disambCatPattern = Pattern
			.compile("\\{\\{disambig\\}\\}");
	private InfoBox infoBox = null;
	
	public WikiTextParser(String wtext) {
		wikiText = wtext;
		Matcher matcher = redirectPattern.matcher(wikiText);
		if (matcher.find()) {
			redirect = true;
			if (matcher.groupCount() == 1)
				redirectString = matcher.group(1);
		}
		matcher = stubPattern.matcher(wikiText);
		stub = matcher.find();
		matcher = disambCatPattern.matcher(wikiText);
		disambiguation = matcher.find();
	}
	
	public boolean isRedirect() {
		return redirect;
	}
	
	public boolean isStub() {
		return stub;
	}
	
	public String getRedirectText() {
		return redirectString;
	}
	
	public String getText() {
		return wikiText;
	}
	
	public Vector<String> getCategories() {
		if (pageCats == null)
			parseCategories();
		return pageCats;
	}
	
	public Vector<String> getLinks() {
		if (pageLinks == null)
			parseLinks();
		return pageLinks;
	}
	
	private void parseCategories() {
		pageCats = new Vector<String>();
		Pattern catPattern = Pattern.compile("\\[\\[" + CATEGORY_PREFIX
				+ "(.*?)\\]\\]", Pattern.MULTILINE);
		Matcher matcher = catPattern.matcher(wikiText);
		while (matcher.find()) {
			String[] temp = matcher.group(1).split("\\|");
			pageCats.add(temp[0]);
		}
	}
	
	private void parseLinks() {
		pageLinks = new Vector<String>();
		
		Pattern catPattern = Pattern.compile("\\[\\[(.*?)\\]\\]",
				Pattern.MULTILINE);
		Matcher matcher = catPattern.matcher(wikiText);
		while (matcher.find()) {
			String[] temp = matcher.group(1).split("\\|");
			if (temp == null || temp.length == 0)
				continue;
			String link = temp[0];
			if (link.contains(":") == false) {
				pageLinks.add(link);
			}
		}
	}
	
	public String getPlainText() throws Exception {
		/*
		 * if (wikiText.startsWith("#REDIRECT") ||
		 * wikiText.startsWith("#Redirect") || wikiText.startsWith("#redirect")
		 * || wikiText.startsWith("File:") || wikiText.startsWith("Template:")
		 * || wikiText.startsWith("Wikipedia:") ||
		 * wikiText.startsWith("Category:") || wikiText.startsWith("Portal:") ||
		 * wikiText.startsWith("User:") || wikiText.startsWith("MediaWiki:") ||
		 * wikiText.startsWith("Book:") || wikiText.startsWith("Special:") ||
		 * wikiText.startsWith("Media:") || wikiText.startsWith("Help:")) return
		 * null; // Antar att du vill g�ra n�got annat h�r
		 */
		
		String text = wikiText.replaceAll("&gt;", ">");
		text = text.replaceAll("&lt;", "<");
		text = text.replaceAll("\\&nbsp;", " "); // Hard space
		text = text.replaceAll("\\&amp;", "&"); // Ampersand
		text = text.replaceAll("\\&[mn]dash;", "-"); // Dashes
		
		text = text.replaceAll("<ref>.*?</ref>", "");
		text = text.replaceAll("<math>.*?</math>", "");
		text = text.replaceAll("</?.*?>", "");
		text = clearWikiTag(text, "{{", "}}");
		text = clearWikiTag(text, "{|", "|}");
		
		// "[[sdfg:dsfdf]]" - > ""
		text = text.replaceAll("\\[\\[[^\\[]*:[^\\[]*[^\\]\\]]{1}\\]\\]", "");
		// "[[sdfg|dsfdf]]" - > "dsfdf"
		text = text.replaceAll("\\[\\[[^\\[\\]\\|]*?\\|([^\\[\\]\\|]+)\\]\\]",
				"$1");
		// "[[sdfgdsfdf]]" - > "sdfgdsfdf"
		text = text.replaceAll("\\[\\[([^\\[\\]\\|]+?)\\]\\]", "$1");
		// "[sdfg dsfdf]" - > "dsfdf"
		text = text.replaceAll("\\[[^\\[\\]\\ ]*?\\ ([^\\[\\]]+)\\]", "$1");
		// "[sdfg]" - > "sdfg"
		text = text.replaceAll("\\[([^\\[\\]]*?)\\]", "$1");
		
		text = clearWikiTag(text, "[[", "]]"); // Remove remaining [[*]]
												// (remains from nestled, at
												// least we tried)
		
		text = text.replaceAll("\\s(.*?)\\|(\\w+\\s)", " $2");
		text = text.replaceAll("\\'+", "");
		text = text.replaceAll("={1,6}(.*?)={1,6}", "$1");
		
		return text;
	}
	
	public static String clearWikiTag(String str, String startTag,
			String stopTag) {
		int position = 0;
		int stringLength = str.length();
		
		if (startTag.length() != stopTag.length()
				|| str.length() <= startTag.length())
			return str;
		
		int counter = 0;
		StringBuilder sb = new StringBuilder();
		
		while (true) {
			boolean b = true;
			String substr = str
					.substring(position, position + stopTag.length());
			if (substr.equals(stopTag)) {
				counter--;
				position += stopTag.length() - 1;
				b = false;
			} else if (substr.equals(startTag)) {
				counter++;
				position += stopTag.length() - 1;
			}
			if (counter == 0 && b) {
				sb.append(substr.substring(0, 1));
			}
			position++;
			if (stringLength < position + stopTag.length())
				return sb.toString();
		}
	}
	
	public InfoBox getInfoBox() {
		// parseInfoBox is expensive. Doing it only once like other parse*
		// methods
		if (infoBox == null)
			infoBox = parseInfoBox();
		return infoBox;
	}
	
	private InfoBox parseInfoBox() {
		String INFOBOX_CONST_STR = "{{Infobox";
		int startPos = wikiText.indexOf(INFOBOX_CONST_STR);
		if (startPos < 0)
			return null;
		int bracketCount = 2;
		int endPos = startPos + INFOBOX_CONST_STR.length();
		for (; endPos < wikiText.length(); endPos++) {
			switch (wikiText.charAt(endPos)) {
				case '}':
					bracketCount--;
					break;
				case '{':
					bracketCount++;
					break;
				default:
			}
			if (bracketCount == 0)
				break;
		}
		if (endPos + 1 >= wikiText.length())
			return null;
		// This happens due to malformed Infoboxes in wiki text. See Issue #10
		// Giving up parsing is the easier thing to do.
		String infoBoxText = wikiText.substring(startPos, endPos + 1);
		infoBoxText = stripCite(infoBoxText); // strip clumsy {{cite}} tags
		// strip any html formatting
		infoBoxText = infoBoxText.replaceAll("&gt;", ">");
		infoBoxText = infoBoxText.replaceAll("&lt;", "<");
		infoBoxText = infoBoxText.replaceAll("<ref.*?>.*?</ref>", " ");
		infoBoxText = infoBoxText.replaceAll("</?.*?>", " ");
		return new InfoBox(infoBoxText);
	}
	
	private String stripCite(String text) {
		String CITE_CONST_STR = "{{cite";
		int startPos = text.indexOf(CITE_CONST_STR);
		if (startPos < 0)
			return text;
		int bracketCount = 2;
		int endPos = startPos + CITE_CONST_STR.length();
		for (; endPos < text.length(); endPos++) {
			switch (text.charAt(endPos)) {
				case '}':
					bracketCount--;
					break;
				case '{':
					bracketCount++;
					break;
				default:
			}
			if (bracketCount == 0)
				break;
		}
		text = text.substring(0, startPos - 1) + text.substring(endPos);
		return stripCite(text);
	}
	
	public boolean isDisambiguationPage() {
		return disambiguation;
	}
	
	public String getTranslatedTitle(String languageCode) {
		Pattern pattern = Pattern.compile("^\\[\\[" + languageCode
				+ ":(.*?)\\]\\]$", Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(wikiText);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}
	
}