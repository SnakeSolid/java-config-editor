package ru.sname.config.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import ru.sname.config.model.ConfigModel;
import ru.sname.config.worker.CollectorListWorker;
import ru.sname.config.worker.LoadConfigWorker;
import ru.sname.config.worker.ServerListWorker;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class WorkerExecutor {

	@Autowired
	private ConfigModel model;

	@Autowired
	private SiuService service;

	public void executeLoadConfig(String serverName, String collectorName) {
		LoadConfigWorker worker = new LoadConfigWorker();
		worker.setService(service);
		worker.setServer(serverName);
		worker.setCollector(collectorName);
		worker.setDocument(model.getConfigurationModel());
		worker.setStatusDocument(model.getStatusModel());
		worker.execute();
	}

	public void executeServerList() {
		ServerListWorker worker = new ServerListWorker();
		worker.setService(service);
		worker.setModel(model.getServersModel());
		worker.setStatusDocument(model.getStatusModel());
		worker.execute();
	};

	public void executeCollectorList(String serverName) {
		CollectorListWorker worker = new CollectorListWorker();
		worker.setService(service);
		worker.setModel(model.getCollectorsModel());
		worker.setServerName(serverName);
		worker.setStatusDocument(model.getStatusModel());
		worker.execute();
	};

}
