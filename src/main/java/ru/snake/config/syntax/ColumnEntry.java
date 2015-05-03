package ru.snake.config.syntax;

import java.util.Collection;
import java.util.LinkedList;

public class ColumnEntry {

	private String type;
	private String reference;
	private String defaultValue;

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

	public String getDefaultValue() {
		return defaultValue;
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

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setValues(Collection<String> values) {
		this.values = values;
	}

	public void addValue(String value) {
		values.add(value);
	}

}
