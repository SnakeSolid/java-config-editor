package ru.snake.config.syntax.error;

public class InvalidCategoryError extends SyntaxError {

	private String subcomponent;
	private String givenCategory;
	private String expectedCategory;

	public String getSubcomponent() {
		return subcomponent;
	}

	public String getGivenCategory() {
		return givenCategory;
	}

	public String getExpectedCategory() {
		return expectedCategory;
	}

	public void setSubcomponent(String subcomponent) {
		this.subcomponent = subcomponent;
	}

	public void setGivenCategory(String givenCategory) {
		this.givenCategory = givenCategory;
	}

	public void setExpectedCategory(String expectedCategory) {
		this.expectedCategory = expectedCategory;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Invalid component ");
		builder.append(subcomponent);
		builder.append(" category, expected ");
		builder.append(expectedCategory);
		builder.append(" but ");
		builder.append(givenCategory);
		builder.append(" given in ");
		builder.append(location);

		return builder.toString();
	}

}
