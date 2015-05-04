package ru.snake.config.syntax.error;

public class TooManyColumnsError extends SyntaxError {

	private String attribute;

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	@Override
	public ErrorLevel getLevel() {
		return ErrorLevel.WARNING;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Too many columns for attrubite ");
		builder.append(attribute);
		builder.append(" in ");
		builder.append(location);

		return builder.toString();
	}

}
