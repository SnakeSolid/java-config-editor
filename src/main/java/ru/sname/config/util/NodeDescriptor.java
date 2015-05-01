package ru.sname.config.util;

public class NodeDescriptor {

	private String name;
	private int offset;
	private boolean mark;

	public NodeDescriptor(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public boolean isMark() {
		return mark;
	}

	public void setMark(boolean mark) {
		this.mark = mark;
	}

	@Override
	public String toString() {
		return name;
	}

}
