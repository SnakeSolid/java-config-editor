package ru.sname.config.worker;

import java.text.MessageFormat;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.text.StyledDocument;

import ru.sname.config.worker.util.StatusAppender;

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
		StatusAppender appender = new StatusAppender(statusDocument, row);
		SwingUtilities.invokeLater(appender);
	}

}
