package ru.snake.config.syntax.error;

public class TooManyValuesError extends SyntaxError {

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
		builder.append("Multiple walues for single valued attrubite ");
		builder.append(attribute);
		builder.append(" in ");
		builder.append(location);

		return builder.toString();
	}

}
