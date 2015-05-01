package ru.sname.config.util;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextAppender extends AbstractAppender {

	private static final Logger logger = LoggerFactory
			.getLogger(TextAppender.class);

	private final String text;

	public TextAppender(StyledDocument document, String message) {
		super(document);

		this.text = message;
	}

	@Override
	public void run() {
		try {
			document.insertString(document.getLength(), text,
					Attributes.DEFAULT);

			trimToSize();
		} catch (BadLocationException e) {
			logger.warn("Can not append log line", e.getMessage());
		}
	}

}
