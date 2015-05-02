package ru.sname.config.syntax;

public class AttributeEntry {

	private String name;
	private boolean required;
	private boolean multiValued;

	public String getName() {
		return name;
	}

	public boolean isRequired() {
		return required;
	}

	public boolean isMultiValued() {
		return multiValued;
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

}
