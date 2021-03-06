package ru.snake.config.worker;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.snake.config.util.Attributes;

public class LoadFileWorker extends AbstractConfigWorker {

	private static final Logger logger = LoggerFactory
			.getLogger(LoadFileWorker.class);

	private File file;
	private StyledDocument document;
	private StringBuilder content;

	public void setFile(File file) {
		this.file = file;
	}

	public void setDocument(StyledDocument document) {
		this.document = document;
	}

	@Override
	protected Void doInBackground() throws Exception {
		int fileSize = (int) file.length();
		char buffer[] = new char[4096];

		content = new StringBuilder(fileSize);

		try (FileReader reader = new FileReader(file)) {
			while (true) {
				int len = reader.read(buffer);

				if (len == -1 || len == 0) {
					break;
				}

				content.append(buffer, 0, len);
			}
		} catch (IOException e) {
			warn("Can not read file content, caused by {0}", e.getMessage());
			logger.error("Can not read file content", e);
		}

		return null;
	}

	@Override
	protected void done() {

		try {
			document.remove(0, document.getLength());

			if (content != null) {
				document.insertString(0, content.toString(), Attributes.DEFAULT);
			}
		} catch (BadLocationException e) {
			logger.error(e.getMessage(), e);
		}
	}

}
