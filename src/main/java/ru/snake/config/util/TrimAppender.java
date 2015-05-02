package ru.snake.config.util;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrimAppender extends AbstractAppender {

	private static final Logger logger = LoggerFactory
			.getLogger(TrimAppender.class);

	public TrimAppender(StyledDocument document) {
		super(document);
	}

	@Override
	public void run() {
		try {
			trimAll();
		} catch (BadLocationException e) {
			logger.warn("Can not trim document", e.getMessage());
		}
	}

}
