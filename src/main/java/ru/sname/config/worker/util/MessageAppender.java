package ru.sname.config.worker.util;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageAppender implements Runnable {

	private static final Logger logger = LoggerFactory
			.getLogger(MessageAppender.class);

	private static final int MAX_DOCUMENT_SIZE = 8192;
	private static final DateFormat DATE_FORMATTER;

	static {
		DATE_FORMATTER = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
				DateFormat.MEDIUM);
	}

	private final StyledDocument document;
	private final String message;

	public MessageAppender(StyledDocument document, String message) {
		this.document = document;
		this.message = message;
	}

	@Override
	public void run() {
		try {
			execute();
		} catch (BadLocationException e) {
			logger.warn("Can not append log line", e.getMessage());
		}
	}

	private void execute() throws BadLocationException {
		int length = document.getLength();

		if (length > MAX_DOCUMENT_SIZE) {
			int trimStart = length - MAX_DOCUMENT_SIZE;
			String part = document.getText(trimStart, length);
			Matcher matcher = Patterns.LINE.matcher(part);

			if (matcher.find(trimStart)) {
				document.remove(0, matcher.end());
			} else {
				document.remove(0, length);
			}
		}

		String dateText = DATE_FORMATTER.format(Calendar.getInstance()
				.getTime());

		document.insertString(document.getLength(), dateText, Attributes.STRONG);
		document.insertString(document.getLength(), ": ", Attributes.LIGHT);
		document.insertString(document.getLength(), message, Attributes.DEFAULT);
		document.insertString(document.getLength(), "\n", Attributes.DEFAULT);
	}

}
