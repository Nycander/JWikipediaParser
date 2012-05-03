package net.java.textilej.parser;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class TextDocumentBuilder extends DocumentBuilder {
	private static final String EXTENDED_IMAGE_REGEX = "^File?\\:.*";
	private static final String CATEGORY_PREFIX = "Kategori:";
	private static final String TRANSLATION_REGEX = "^[a-z\\-]{2,}\\:.*";
	
	private final StringWriter stringWriter;
	private List<String> categories = new ArrayList<String>();
	
	public TextDocumentBuilder(StringWriter stringWriter) {
		this.stringWriter = stringWriter;
	}
	
	public List<String> getCategories() {
		return categories;
	}
	
	@Override
	public void characters(String text) {
		stringWriter.append(text);
	}
	
	@Override
	public void link(Attributes attributes, String hrefOrHashName, String text) {
		if (text.startsWith(CATEGORY_PREFIX)) {
			categories.add(text.substring(CATEGORY_PREFIX.length()));
			return;
		}
		
		if (text.matches(TRANSLATION_REGEX)) {
			// Ignore translated pages.
			return;
		}
		
		String title = attributes.getTitle();
		if (title != null && title.matches(EXTENDED_IMAGE_REGEX)) {
			// Ignore links that are really images
			return;
		}
		
		stringWriter.append(text);
	}
	
	@Override
	public void acronym(String text, String definition) {
		stringWriter.append(text).append(" (").append(definition).append(')');
	}
	
	@Override
	public void lineBreak() {
		stringWriter.append("\n");
	}
	
	@Override
	public void beginHeading(int level, Attributes attributes) {
	}
	
	@Override
	public void endHeading() {
		stringWriter.append("\n\n");
	}
	
	@Override
	public void charactersUnescaped(String literal) {
		// These are mainly HTML tags, like <small> and stuff. Ignore them.
	}
	
	@Override
	public void beginDocument() {
		// Ignore
	}
	
	@Override
	public void endDocument() {
		// Ignore
	}
	
	private BlockType blockType;
	
	@Override
	public void beginBlock(BlockType type, Attributes attributes) {
		this.blockType = type;
	}
	
	@Override
	public void endBlock() {
		if (blockType == BlockType.PARAGRAPH)
			stringWriter.append("\n\n");
	}
	
	@Override
	public void beginSpan(SpanType type, Attributes attributes) {
		// Ignore
	}
	
	@Override
	public void endSpan() {
		// Ignore
	}
	
	@Override
	public void entityReference(String entity) {
		// Ignore
	}
	
	@Override
	public void image(Attributes attributes, String url) {
		// Ignore
	}
	
	@Override
	public void imageLink(Attributes linkAttributes,
			Attributes imageAttributes, String href, String imageUrl) {
		// Ignore
	}
}
