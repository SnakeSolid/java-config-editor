package ru.snake.config.syntax;

public class SubcomponentEntry {

	private String name;
	private String category;
	private boolean required;

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	public boolean isRequired() {
		return required;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

}
