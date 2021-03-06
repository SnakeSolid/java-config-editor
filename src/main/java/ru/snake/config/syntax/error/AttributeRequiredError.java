package ru.snake.config.syntax.error;

public class AttributeRequiredError extends SyntaxError {

	private String attribute;

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	@Override
	public ErrorLevel getLevel() {
		return ErrorLevel.ERROR;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Attribute ");
		builder.append(attribute);
		builder.append(" required in ");
		builder.append(location);

		return builder.toString();
	}

}
