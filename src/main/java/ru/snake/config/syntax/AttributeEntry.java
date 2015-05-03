package ru.snake.config.syntax;

import java.util.LinkedList;
import java.util.List;

public class AttributeEntry {

	private String name;
	private boolean required;
	private boolean multiValued;

	private List<ColumnEntry> columns;

	public AttributeEntry() {
		columns = new LinkedList<ColumnEntry>();
	}

	public String getName() {
		return name;
	}

	public boolean isRequired() {
		return required;
	}

	public boolean isMultiValued() {
		return multiValued;
	}

	public List<ColumnEntry> getColumns() {
		return columns;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public void setMultiValued(boolean multi) {
		this.multiValued = multi;
	}

	public void addColumn(ColumnEntry entry) {
		columns.add(entry);
	}

}
