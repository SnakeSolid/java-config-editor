package ru.snake.config.syntax;

import java.util.Collection;
import java.util.LinkedList;

public class ComponentEntry {

	private String className;
	private String packageName;
	private String category;

	private Collection<SubcomponentEntry> subcomponents;
	private Collection<AttributeEntry> attributes;

	public ComponentEntry() {
		subcomponents = new LinkedList<SubcomponentEntry>();
		attributes = new LinkedList<AttributeEntry>();
	}

	public String getClassName() {
		return className;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getCategory() {
		return category;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void addSubcomponent(SubcomponentEntry entry) {
		subcomponents.add(entry);
	}

	public void addAttribute(AttributeEntry entry) {
		attributes.add(entry);
	}

	public Collection<SubcomponentEntry> getSubcomponents() {
		return subcomponents;
	}

	public Collection<AttributeEntry> getAttributes() {
		return attributes;
	}

}
