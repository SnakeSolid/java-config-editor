package ru.sname.config.task;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.PostConstruct;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.sname.config.model.ConfigModel;
import ru.sname.config.service.SiuService;
import ru.sname.config.worker.util.DetatilsDescriptor;
import ru.sname.config.worker.util.UpdateDetailsRunnable;

import com.hp.siu.utils.ClientException;

@Component
public class DetailsUpdateTask implements ListDataListener {

	private static final Logger logger = LoggerFactory
			.getLogger(DetailsUpdateTask.class);

	@Autowired
	private ConfigModel model;

	@Autowired
	private SiuService service;

	private volatile boolean initialized;
	private volatile String serverName;
	private volatile String processName;

	private Object lock;

	@PostConstruct
	private void initialize() {
		model.getCollectorsModel().addListDataListener(this);

		initialized = false;
		lock = new Object();
	}

	@Scheduled(cron = "* * * * * *")
	public void execute() {
		if (!initialized) {
			return;
		}

		synchronized (lock) {
			updateDetails();
		}
	}

	private void updateDetails() {
		DetatilsDescriptor descriptor = new DetatilsDescriptor();

		try {
			service.getProcessDetails(serverName, processName, descriptor);
		} catch (ClientException e) {
			logger.warn("Error updating datails", e);
		}

		UpdateDetailsRunnable runnable = new UpdateDetailsRunnable();
		runnable.setModel(model.getDetailsModel());
		runnable.setDescriptor(descriptor);

		try {
			SwingUtilities.invokeAndWait(runnable);
		} catch (InvocationTargetException | InterruptedException e) {
			logger.warn("Error updating datails", e);
		}
	}

	@Override
	public void contentsChanged(ListDataEvent event) {
		String serverName = (String) model.getServersModel().getSelectedItem();
		String processName = (String) model.getCollectorsModel()
				.getSelectedItem();

		if (serverName == null) {
			initialized = false;

			return;
		}

		if (processName == null) {
			initialized = false;

			return;
		}

		synchronized (lock) {
			this.serverName = serverName;
			this.processName = processName;

			initialized = true;
		}
	}

	@Override
	public void intervalAdded(ListDataEvent event) {
	}

	@Override
	public void intervalRemoved(ListDataEvent event) {
	}

}
