package ru.sname.config.util;

public class NodeDescriptor {

	private String name;
	private int startsFrom;
	private int endsWith;
	private boolean mark;

	public NodeDescriptor(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getStartsFrom() {
		return startsFrom;
	}

	public int getEndsWith() {
		return endsWith;
	}

	public boolean isMark() {
		return mark;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStartsFrom(int startsFrom) {
		this.startsFrom = startsFrom;
	}

	public void setEndsWith(int endsWith) {
		this.endsWith = endsWith;
	}

	public void setMark(boolean mark) {
		this.mark = mark;
	}

	@Override
	public String toString() {
		return name;
	}

}
