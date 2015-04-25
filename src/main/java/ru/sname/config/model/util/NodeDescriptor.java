package ru.sname.config.model.util;

public class NodeDescriptor {

	private String name;
	private boolean mark;

	public NodeDescriptor(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean isMark() {
		return mark;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMark(boolean mark) {
		this.mark = mark;
	}

	@Override
	public String toString() {
		return name;
	}

}
