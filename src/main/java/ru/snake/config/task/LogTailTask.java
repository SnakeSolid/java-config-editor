package ru.snake.config.task;

import javax.annotation.PostConstruct;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.omg.CORBA.LongHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.snake.config.model.ConfigModel;
import ru.snake.config.service.SiuService;
import ru.snake.config.service.WorkerExecutor;
import ru.snake.config.util.TextAppender;
import ru.snake.config.util.TrimAppender;

import com.hp.siu.utils.ClientException;
import com.hp.siu.utils.ClientFileNotFoundException;
import com.hp.siu.utils.SafeFileHandlerClient;

@Component
public class LogTailTask implements ListDataListener, LogTailHandler {

	private static final Logger logger = LoggerFactory
			.getLogger(LogTailTask.class);

	private static final int MAX_BUFFER_SIZE = 65536;

	@Autowired
	private ConfigModel model;

	@Autowired
	private SiuService service;

	@Autowired
	private WorkerExecutor executor;

	private volatile boolean initialized;
	private volatile String fileName;
	private volatile SafeFileHandlerClient fileTailer;
	private volatile long fileOffset;

	private byte[] buffer;
	private Object lock;

	@PostConstruct
	private void initialize() {
		model.getCollectorsModel().addListDataListener(this);

		initialized = false;

		buffer = new byte[MAX_BUFFER_SIZE];
		lock = new Object();
	}

	@Scheduled(cron = "* * * * * *")
	public void execute() {
		if (!initialized) {
			return;
		}

		synchronized (lock) {
			readFile();
		}
	}

	private void readFile() {
		long fileSize;

		try {
			fileSize = fileTailer.length(fileName);
		} catch (ClientFileNotFoundException e) {
			logger.debug("File {} does not found", fileName, e);

			return;
		} catch (ClientException e) {
			logger.warn("Error occured while getting file size", e);

			return;
		}

		if (fileSize < fileOffset) {
			if (fileSize < MAX_BUFFER_SIZE) {
				fileOffset = 0;
			} else {
				fileOffset = fileSize - MAX_BUFFER_SIZE;
			}
		} else if (fileOffset < fileSize - MAX_BUFFER_SIZE) {
			fileOffset = fileSize - MAX_BUFFER_SIZE;
		} else if (fileOffset == fileSize) {
			return;
		}

		String data;

		try {
			LongHolder longHolder = new LongHolder();
			int blockSize = (int) (fileSize - fileOffset);
			int readedBytes = fileTailer.readBytes(fileName, fileOffset,
					longHolder, buffer, 0, blockSize);

			data = new String(buffer, 0, readedBytes);

			fileOffset += readedBytes;
		} catch (ClientFileNotFoundException e) {
			logger.debug("File {} does not found", fileSize, e);

			return;
		} catch (ClientException e) {
			logger.warn("Error occured while reading file", e);

			return;
		}

		TextAppender appender = new TextAppender(model.getLogModel(), data);
		SwingUtilities.invokeLater(appender);
	}

	@Override
	public void contentsChanged(ListDataEvent event) {
		synchronized (lock) {
			this.fileName = null;
			this.fileTailer = null;
			this.fileOffset = 0;

			initialized = false;
		}

		String serverName = (String) model.getServersModel().getSelectedItem();
		String processName = (String) model.getCollectorsModel()
				.getSelectedItem();

		if (serverName == null || serverName.isEmpty()) {
			return;
		}

		if (processName == null || processName.isEmpty()) {
			return;
		}

		executor.executeByteTailer(processName, serverName, this);

		TrimAppender appender = new TrimAppender(model.getLogModel());
		SwingUtilities.invokeLater(appender);
	}

	@Override
	public void intervalAdded(ListDataEvent event) {
	}

	@Override
	public void intervalRemoved(ListDataEvent event) {
	}

	@Override
	public void byteTailerCreated(String serverName, String filename,
			SafeFileHandlerClient fileTailer) {
		synchronized (lock) {
			this.fileName = filename;
			this.fileTailer = fileTailer;
			this.fileOffset = 0;

			this.initialized = true;
		}
	}

}
