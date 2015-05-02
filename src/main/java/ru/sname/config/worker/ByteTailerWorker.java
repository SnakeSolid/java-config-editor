package ru.sname.config.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.sname.config.service.SiuService;
import ru.sname.config.task.LogTailHandler;

import com.hp.siu.utils.ClientException;

public class ByteTailerWorker extends AbstractConfigWorker {

	private static final Logger logger = LoggerFactory
			.getLogger(DebugProcessWorker.class);

	private String serverName;
	private SiuService service;
	private LogTailHandler handler;

	public void setHandler(LogTailHandler handler) {
		this.handler = handler;
	}

	public void setService(SiuService service) {
		this.service = service;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	@Override
	protected Void doInBackground() {
		info("Creating byte tailer on {0}...", serverName);

		try {
			handler.byteTailerCreated(serverName,
					service.createByteTailer(serverName));
		} catch (ClientException e) {
			warn("Failed to create byte tailer on {0}, caused by: {1}.",
					serverName, e.getMessage());
			logger.warn("Failed to create byte tailer", e);
		}

		info("Byte tailer on {0} has been created.", serverName);

		return null;
	}

}
