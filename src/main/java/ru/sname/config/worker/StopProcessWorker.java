package ru.sname.config.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.siu.utils.ClientException;

public class StopProcessWorker extends AbstractSuiWorker {

	private static final Logger logger = LoggerFactory
			.getLogger(StopProcessWorker.class);

	@Override
	protected Void doInBackground() {
		append("Stopping collector {0}...", collectorName);

		try {
			service.stopProcess(serverName, collectorName);
		} catch (ClientException e) {
			logger.warn(e.getMessage(), e);
		}

		append("Collector {0} has been stopped.", collectorName);

		return null;
	}

}