package ru.sname.config.worker.util;

import java.util.regex.Matcher;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public abstract class AbstractAppender implements Runnable {

	protected static final int MAX_DOCUMENT_SIZE = 65536;

	protected final StyledDocument document;

	public AbstractAppender(StyledDocument document) {
		this.document = document;
	}

	protected void trimToSize() throws BadLocationException {
		int length = document.getLength();

		if (length > MAX_DOCUMENT_SIZE) {
			int trimStart = length - MAX_DOCUMENT_SIZE;
			String part = document.getText(trimStart, MAX_DOCUMENT_SIZE);
			Matcher matcher = Patterns.LINE.matcher(part);

			if (matcher.find()) {
				document.remove(0, trimStart + matcher.end());
			} else {
				document.remove(0, trimStart);
			}
		}
	}

}
