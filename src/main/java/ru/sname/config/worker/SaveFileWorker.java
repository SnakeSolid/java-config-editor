package ru.sname.config.worker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaveFileWorker extends SwingWorker<Void, Void> {

	private static final Logger logger = LoggerFactory
			.getLogger(SaveFileWorker.class);

	private File file;
	private String content;

	public void setFile(File file) {
		this.file = file;
	}

	public void setContent(String content) {
		this.content = content;
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
