package ru.snake.config.syntax.error;

public class UnusedAttributeError extends SyntaxError {

	private String attribute;

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Unused attribute ");
		builder.append(attribute);
		builder.append(" in ");
		builder.append(location);

		return builder.toString();
	}

}
