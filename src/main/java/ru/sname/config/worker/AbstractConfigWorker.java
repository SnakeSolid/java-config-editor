package ru.sname.config.worker;

import java.text.MessageFormat;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.text.StyledDocument;

import ru.sname.config.worker.util.MessageAppender;

public abstract class AbstractConfigWorker extends SwingWorker<Void, Void> {

	protected StyledDocument statusDocument;

	public void setStatusDocument(StyledDocument statusDocument) {
		this.statusDocument = statusDocument;
	}

	protected void append(String message, Object... params) {
		if (statusDocument == null) {
			return;
		}

		String row = MessageFormat.format(message, params);
		MessageAppender appender = new MessageAppender(statusDocument, row);
		SwingUtilities.invokeLater(appender);
	}

}
