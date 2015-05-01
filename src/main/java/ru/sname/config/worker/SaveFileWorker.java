package ru.sname.config.worker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaveFileWorker extends AbstractConfigWorker {

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
			warn("Can not write file, caused by {0}", e.getMessage());
			logger.error(e.getMessage(), e);
		}

		return null;
	}

}
