package ru.sname.config.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.siu.utils.ClientException;

public class StopProcessWorker extends AbstractSuiWorker {

	private static final Logger logger = LoggerFactory
			.getLogger(StopProcessWorker.class);

	@Override
	protected Void doInBackground() {
		info("Stopping process {0}...", collectorName);

		try {
			service.stopProcess(serverName, collectorName);
		} catch (ClientException e) {
			warn("Failed to stop process {0}, caused by: {1}.", collectorName,
					e.getMessage());
			logger.warn(e.getMessage(), e);

			return null;
		}

		info("Process {0} has been stopped.", collectorName);

		return null;
	}

}
