package ru.sname.config.worker.util;

import java.text.DateFormat;
import java.util.Calendar;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageAppender extends AbstractAppender {

	private static final Logger logger = LoggerFactory
			.getLogger(MessageAppender.class);

	private static final DateFormat DATE_FORMATTER;

	static {
		DATE_FORMATTER = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
				DateFormat.MEDIUM);
	}

	private final String message;

	public MessageAppender(StyledDocument document, String message) {
		super(document);

		this.message = message;
	}

	@Override
	public void run() {
		String dateText = DATE_FORMATTER.format(Calendar.getInstance()
				.getTime());

		try {
			document.insertString(document.getLength(), dateText,
					Attributes.STRONG);
			document.insertString(document.getLength(), ": ", Attributes.LIGHT);
			document.insertString(document.getLength(), message,
					Attributes.DEFAULT);
			document.insertString(document.getLength(), "\n",
					Attributes.DEFAULT);

			trimToSize();
		} catch (BadLocationException e) {
			logger.warn("Can not append log line", e.getMessage());
		}
	}

}
