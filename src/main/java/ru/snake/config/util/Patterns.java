package ru.snake.config.util;

import java.util.regex.Pattern;

public class Patterns {

	public static final Pattern LINE;
	public static final Pattern CATEGORY;
	public static final Pattern CATEGORY_DELIMITER;
	public static final Pattern COLUMN_SEPARATOR;
	public static final Pattern IP_ADDRESS;

	static {
		LINE = Pattern.compile("[\\r\\n]+", Pattern.MULTILINE);
		CATEGORY = Pattern.compile("^\\[", Pattern.MULTILINE);
		CATEGORY_DELIMITER = Pattern.compile("/", 0);
		COLUMN_SEPARATOR = Pattern.compile(",", 0);
		IP_ADDRESS = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$", 0);
	}

}
