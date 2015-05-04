package ru.snake.config.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.snake.config.service.SiuService;
import ru.snake.config.task.LogTailHandler;

import com.hp.siu.utils.ClientException;
import com.hp.siu.utils.SafeFileHandlerClient;

public class ByteTailerWorker extends AbstractConfigWorker {

	private static final Logger logger = LoggerFactory
			.getLogger(DebugProcessWorker.class);

	private String processName;
	private String serverName;
	private SiuService service;
	private LogTailHandler handler;

	public void setHandler(LogTailHandler handler) {
		this.handler = handler;
	}

	public void setService(SiuService service) {
		this.service = service;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	@Override
	protected Void doInBackground() {
		info("Trying to get logfile name for {0} on {1}...", processName,
				serverName);

		String fileName;

		try {
			fileName = service.getLogFileName(serverName, processName);
		} catch (ClientException e) {
			warn("Failed to get logfile name for {0}, caused by: {1}.",
					serverName, e.getMessage());
			logger.warn("Failed to get logfile name", e);

			return null;
		}

		info("Logfile name received.");
		info("Creating byte tailer on {0}...", serverName);

		SafeFileHandlerClient byteTailer;

		try {
			byteTailer = service.createByteTailer(serverName);
		} catch (ClientException e) {
			warn("Failed to create byte tailer on {0}, caused by: {1}.",
					serverName, e.getMessage());
			logger.warn("Failed to create byte tailer", e);

			return null;
		}

		handler.byteTailerCreated(serverName, fileName, byteTailer);

		info("Byte tailer on {0} has been created.", serverName);

		return null;
	}

}
