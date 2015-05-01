package ru.sname.config.worker;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.sname.config.model.StringBoxModel;
import ru.sname.config.service.SiuService;

import com.hp.siu.utils.ClientException;

public class CollectorListWorker extends AbstractConfigWorker {

	private static final Logger logger = LoggerFactory
			.getLogger(CollectorListWorker.class);

	private SiuService service;
	private StringBoxModel model;
	private String serverName;

	private List<String> collectors;

	public void setService(SiuService service) {
		this.service = service;
	}

	public void setModel(StringBoxModel model) {
		this.model = model;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	@Override
	protected Void doInBackground() {
		if (serverName == null) {
			append("Server must be selected.");

			return null;
		}

		append("Receiving collector list for {0}...", serverName);

		try {
			collectors = service.getCollectors(serverName);
		} catch (ClientException e) {
			logger.warn("Error while receiving collector list", e);

			return null;
		}

		append("Collector list was received.");

		return null;
	}

	@Override
	protected void done() {
		if (collectors == null) {
			return;
		}

		model.setList(collectors);

		if (collectors.size() == 1) {
			String serverName = collectors.iterator().next();

			model.setSelectedItem(serverName);
		}
	}

}
