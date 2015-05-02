package ru.snake.config.syntax.error;

public class UnusedSubcomponentError extends SyntaxError {

	private String subcomponent;

	public String getSubcomponent() {
		return subcomponent;
	}

	public void setSubcomponent(String subcomponent) {
		this.subcomponent = subcomponent;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Unused subcomponent ");
		builder.append(subcomponent);
		builder.append(" in ");
		builder.append(location);

		return builder.toString();
	}

}
