package ru.snake.config.syntax.error;

public class SyntaxError {

	protected String path;
	protected String location;

	public String getPath() {
		return path;
	}

	public String getLocation() {
		return location;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Syntax error in ");
		builder.append(location);

		return builder.toString();
	}

}
