package ru.snake.config.model.data;

import ru.snake.config.syntax.error.ErrorLevel;

public class ProblemItem {

	private ErrorLevel level;
	private String description;
	private String path;
	private String location;

	public ErrorLevel getLevel() {
		return level;
	}

	public String getDescription() {
		return description;
	}

	public String getPath() {
		return path;
	}

	public String getLocation() {
		return location;
	}

	public void setLevel(ErrorLevel level) {
		this.level = level;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
