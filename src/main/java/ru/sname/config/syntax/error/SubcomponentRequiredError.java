package ru.sname.config.syntax.error;

public class SubcomponentRequiredError extends SyntaxError {

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
		builder.append("Subcomponent ");
		builder.append(subcomponent);
		builder.append(" required in ");
		builder.append(location);

		return builder.toString();
	}

}
