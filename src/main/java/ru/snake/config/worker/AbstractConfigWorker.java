package ru.snake.config.worker;

import java.text.MessageFormat;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.text.StyledDocument;

import ru.snake.config.util.MessageAppender;
import ru.snake.config.util.MessageDisposer;

public abstract class AbstractConfigWorker extends SwingWorker<Void, Void> {

	protected StyledDocument statusDocument;

	public void setStatusDocument(StyledDocument statusDocument) {
		this.statusDocument = statusDocument;
	}

	protected void info(String message, Object... params) {
		if (statusDocument == null) {
			return;
		}

		String row = MessageFormat.format(message, params);
		MessageAppender appender = new MessageAppender(statusDocument, row);
		SwingUtilities.invokeLater(appender);
	}

	protected void warn(String message, Object... params) {
		if (statusDocument == null) {
			return;
		}

		String row = MessageFormat.format(message, params);
		MessageAppender appender = new MessageAppender(statusDocument, row);
		SwingUtilities.invokeLater(appender);

		MessageDisposer disposer = new MessageDisposer(row);
		SwingUtilities.invokeLater(disposer);
	}

}
