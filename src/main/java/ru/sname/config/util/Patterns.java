package ru.sname.config.util;

import java.util.regex.Pattern;

public class Patterns {

	public static final Pattern LINE;
	public static final Pattern CATEGORY;
	public static final Pattern CATEGORY_DELIMITER;

	static {
		LINE = Pattern.compile("[\\r\\n]+", Pattern.MULTILINE);
		CATEGORY = Pattern.compile("^\\[", Pattern.MULTILINE);
		CATEGORY_DELIMITER = Pattern.compile("/", 0);
	}

}
