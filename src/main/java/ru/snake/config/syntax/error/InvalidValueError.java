package ru.snake.config.syntax.error;

public class InvalidValueError extends SyntaxError {

	private String attribute;
	private String type;
	private String value;

	public String getAttribute() {
		return attribute;
	}

	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public ErrorLevel getLevel() {
		return ErrorLevel.WARNING;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Invalid value for ");
		builder.append(attribute);
		builder.append(" expected ");
		builder.append(type);
		builder.append(" but ");
		builder.append(value);
		builder.append(" found in ");
		builder.append(location);

		return builder.toString();
	}

}
