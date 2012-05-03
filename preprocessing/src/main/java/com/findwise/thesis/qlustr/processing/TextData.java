package com.findwise.thesis.qlustr.processing;

import java.util.*;

/**
 * Represents processable text data with meta data.
 * 
 * @author Martin Nycander (martin.nycander@gmail.com)
 */
public class TextData implements CharSequence {
	private String text;
	private List<String> originalText = new ArrayList<String>();
	private Map<String, Object> fields;
	
	public TextData() {
		this("");
	}
	
	public TextData(String text) {
		this(text, new HashMap<String, Object>());
	}
	
	public TextData(String text, Map<String, Object> fields) {
		this.text = text;
		this.fields = fields;
	}
	
	public TextData(TextData textData) {
		text = textData.text;
		fields = new HashMap<String, Object>(textData.fields.size());
		fields.putAll(textData.fields);
		originalText.addAll(textData.originalText);
	}

	public String getText() {
		return text;
	}
	
	public List<String> getOriginalTexts() {
		return originalText;
	}
	
	public void addOriginalText(String text) {
		originalText.add(text);
	}

	public void ensureBackup() {
		// if (originalText.isEmpty())
		// originalText.add(text);
	}

	public void setText(String newText) {
		text = newText;
	}
	
	public Object getField(String key) {
		return fields.get(key);
	}
	
	public boolean hasField(String field) {
		return fields.containsKey(field);
	}
	
	public void putField(String key, Object o) {
		fields.put(key, o);
	}
	
	public Iterable<Map.Entry<String, Object>> fields() {
		return fields.entrySet();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Map<String, T> toMap(TextData[] list, String field) {
		HashMap<String, T> map = new HashMap<String, T>(list.length);
		for (TextData item : list)
			map.put(item.getText(), (T) item.getField(field));
		return map;
	}
	
	@Override
	public String toString() {
		return getText();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TextData other = (TextData) obj;
		if (fields == null) {
			if (other.fields != null)
				return false;
		} else if (!fields.equals(other.fields))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}
	
	@Override
	public int length() {
		return text.length();
	}
	
	@Override
	public char charAt(int index) {
		return text.charAt(index);
	}
	
	@Override
	public CharSequence subSequence(int start, int end) {
		return text.subSequence(start, end);
	}
}
