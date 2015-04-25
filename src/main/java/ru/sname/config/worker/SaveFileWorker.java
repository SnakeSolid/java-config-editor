package ru.sname.config.worker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaveFileWorker extends SwingWorker<Void, Void> {

	private static final Logger logger = LoggerFactory
			.getLogger(SaveFileWorker.class);

	private final File file;

	private String content;

	public SaveFileWorker(File file, StyledDocument document) {
		this.file = file;
		this.content = getContent(document);
	}

	private String getContent(StyledDocument document) {
		try {
			return document.getText(0, document.getLength());
		} catch (BadLocationException e) {
			logger.error(e.getMessage(), e);

			return "";
		}
	}

	@Override
	protected Void doInBackground() throws Exception {
		try (FileWriter writer = new FileWriter(file)) {
			writer.write(content);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

}
