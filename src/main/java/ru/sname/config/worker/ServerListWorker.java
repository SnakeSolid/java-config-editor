package ru.sname.config.worker;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.sname.config.model.StringBoxModel;
import ru.sname.config.service.SiuService;

import com.hp.siu.utils.ClientException;

public class ServerListWorker extends AbstractConfigWorker {

	private static final Logger logger = LoggerFactory
			.getLogger(ServerListWorker.class);

	private SiuService service;
	private StringBoxModel model;

	private List<String> servers;

	public void setService(SiuService service) {
		this.service = service;
	}

	public void setModel(StringBoxModel model) {
		this.model = model;
	}

	@Override
	protected Void doInBackground() {
		append("Receiving server list...");

		try {
			servers = service.getServers();
		} catch (ClientException e) {
			logger.warn("Error while receiving server list", e);

			return null;
		}

		append("Server list was received.");

		return null;
	}

	@Override
	protected void done() {
		if (servers == null) {
			return;
		}

		model.setList(servers);

		if (servers.size() == 1) {
			String serverName = servers.iterator().next();

			model.setSelectedItem(serverName);
		}
	}

}
