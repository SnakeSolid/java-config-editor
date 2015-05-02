package ru.sname.config.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ConfigNode {

	private final String name;
	private final Map<String, List<String>> attributes;
	private final Map<String, ConfigNode> children;

	private ConfigNode parent;

	public ConfigNode(String name) {
		this.name = name;
		this.attributes = new HashMap<String, List<String>>();
		this.children = new HashMap<String, ConfigNode>();
	}

	public String getName() {
		return name;
	}

	public void setValue(String attribute, String value) {
		List<String> list;

		if (attributes.containsKey(attribute)) {
			list = attributes.get(attribute);

			list.clear();
		} else {
			list = new ArrayList<String>();

			attributes.put(attribute, list);
		}

		list.add(value);
	}

	public void setValue(String attribute, Collection<String> value) {
		List<String> list;

		if (attributes.containsKey(attribute)) {
			list = attributes.get(attribute);

			list.clear();
		} else {
			list = new ArrayList<String>();

			attributes.put(attribute, list);
		}

		list.addAll(value);
	}

	public void pushValue(String attribute, String value) {
		List<String> list;

		if (attributes.containsKey(attribute)) {
			list = attributes.get(attribute);
		} else {
			list = new ArrayList<String>();

			attributes.put(attribute, list);
		}

		list.add(value);
	}

	public ConfigNode getParent() {
		return parent;
	}

	public void setParent(ConfigNode parent) {
		this.parent = parent;
	}

	public void addChild(ConfigNode child) {
		String childName = child.getName();

		if (!children.containsKey(childName)) {
			children.put(childName, child);

			child.setParent(this);
		}
	}

	public boolean hasChild(String childName) {
		return children.containsKey(childName);
	}

	public ConfigNode getChild(String childName) {
		return children.get(childName);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(name);
		builder.append(attributes);

		return builder.toString();
	}

	public boolean hasAttribute(String attribute) {
		return attributes.containsKey(attribute);
	}

	public List<String> getValues(String attribute) {
		if (attributes.containsKey(attribute)) {
			return attributes.get(attribute);
		}

		return Collections.emptyList();
	}

	public List<ConfigNode> getChildren() {
		List<ConfigNode> childList = new LinkedList<ConfigNode>();

		for (Entry<String, ConfigNode> entry : children.entrySet()) {
			childList.add(entry.getValue());
		}

		return childList;
	}

	public List<String> getAttributes() {
		List<String> attrList = new LinkedList<String>();

		for (Entry<String, List<String>> entry : attributes.entrySet()) {
			attrList.add(entry.getKey());
		}

		return attrList;
	}

	public String getValue(String attribute) {
		return attributes.get(attribute).iterator().next();
	}

}
