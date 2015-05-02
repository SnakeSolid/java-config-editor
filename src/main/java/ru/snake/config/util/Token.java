package ru.snake.config.util;

public class Token {

	private TokenType type;
	private String text;
	private int start;
	private int length;

	public Token() {
	}

	public TokenType getType() {
		return type;
	}

	public String getText() {
		return text;
	}

	public int getStart() {
		return start;
	}

	public int getLength() {
		return length;
	}

	public void setType(TokenType type) {
		this.type = type;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public void setLength(int length) {
		this.length = length;
	}

}
