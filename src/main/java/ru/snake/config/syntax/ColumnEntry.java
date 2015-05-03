package ru.snake.config.syntax;

import java.util.Collection;
import java.util.LinkedList;

public class ColumnEntry {

	private String type;
	private String reference;

	private Collection<String> values;

	public ColumnEntry() {
		values = new LinkedList<String>();
	}

	public String getType() {
		return type;
	}

	public String getReference() {
		return reference;
	}

	public Collection<String> getValues() {
		return values;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public void addValue(String value) {
		values.add(value);
	}

}
