package ru.sname.config.worker;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadFileWorker extends SwingWorker<Void, Void> {

	private static final Logger logger = LoggerFactory
			.getLogger(LoadFileWorker.class);

	private static final SimpleAttributeSet DEFAULT_STYLE;

	static {
		DEFAULT_STYLE = new SimpleAttributeSet();
	}

	private final File file;
	private final StyledDocument document;

	private StringBuilder content;

	public LoadFileWorker(File file, StyledDocument document) {
		this.file = file;
		this.document = document;
		this.content = new StringBuilder();
	}

	@Override
	protected Void doInBackground() throws Exception {
		char buffer[] = new char[4096];

		try (FileReader reader = new FileReader(file)) {
			while (true) {
				int len = reader.read(buffer);

				if (len == -1 || len == 0) {
					break;
				}

				content.append(buffer, 0, len);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	protected void done() {
		try {
			document.remove(0, document.getLength());
			document.insertString(0, content.toString(), DEFAULT_STYLE);
		} catch (BadLocationException e) {
			logger.error(e.getMessage(), e);
		}
	}

}
