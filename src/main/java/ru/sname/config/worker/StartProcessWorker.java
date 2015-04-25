package ru.sname.config.worker;

import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.sname.config.service.SiuService;

import com.hp.siu.utils.InvalidStateException;

public class StartProcessWorker extends SwingWorker<Void, Void> {

	private static final Logger logger = LoggerFactory
			.getLogger(HighlightWorker.class);

	private SiuService service;
	private String serverName;
	private String collectorName;

	@Override
	protected Void doInBackground() throws Exception {
		try {
			service.stopProcess(serverName, collectorName);
		} catch (InvalidStateException e) {
			logger.warn(e.getMessage(), e);
		}

		service.cleanupProcess(serverName, collectorName);
		service.startProcess(serverName, collectorName);

		return null;
	}

	public void setService(SiuService service) {
		this.service = service;
	}

	public void setServer(String serverName) {
		this.serverName = serverName;
	}

	public void setCollector(String collectorName) {
		this.collectorName = collectorName;
	}

}
