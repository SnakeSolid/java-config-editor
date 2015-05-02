package ru.snake.config.util;

import java.awt.Color;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class Attributes {

	public static final SimpleAttributeSet LIGHT;
	public static final SimpleAttributeSet DEFAULT;
	public static final SimpleAttributeSet STRONG;

	public static final SimpleAttributeSet CATHEGORY;
	public static final SimpleAttributeSet ATTRIBUTE;
	public static final SimpleAttributeSet EQUALS;
	public static final SimpleAttributeSet VALUE;
	public static final SimpleAttributeSet COMMENT;
	public static final SimpleAttributeSet ERROR;

	static {
		LIGHT = new SimpleAttributeSet();
		StyleConstants.setForeground(LIGHT, Color.GRAY);

		DEFAULT = new SimpleAttributeSet();

		STRONG = new SimpleAttributeSet();
		StyleConstants.setBold(STRONG, true);

		CATHEGORY = new SimpleAttributeSet();
		StyleConstants.setForeground(CATHEGORY, Color.BLUE);
		StyleConstants.setBold(CATHEGORY, true);

		ATTRIBUTE = new SimpleAttributeSet();
		StyleConstants.setForeground(ATTRIBUTE, Color.BLACK);
		StyleConstants.setBold(ATTRIBUTE, true);

		EQUALS = new SimpleAttributeSet();
		StyleConstants.setForeground(EQUALS, Color.GRAY);

		VALUE = new SimpleAttributeSet();
		StyleConstants.setForeground(VALUE, Color.BLUE);

		COMMENT = new SimpleAttributeSet();
		StyleConstants.setForeground(COMMENT, Color.GRAY);

		ERROR = new SimpleAttributeSet();
		StyleConstants.setForeground(ERROR, Color.RED);
		StyleConstants.setBold(ERROR, true);
	}

}
