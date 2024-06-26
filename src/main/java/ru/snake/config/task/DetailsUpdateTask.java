package ru.snake.config.task;

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

import com.hp.siu.utils.ClientException;

import ru.snake.config.model.ConfigModel;
import ru.snake.config.service.SiuService;
import ru.snake.config.util.DetatilsDescriptor;
import ru.snake.config.util.UpdateDetailsRunnable;

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
			logger.warn("Error updating details", e);
		}

		UpdateDetailsRunnable runnable = new UpdateDetailsRunnable();
		runnable.setModel(model.getDetailsModel());
		runnable.setDescriptor(descriptor);

		try {
			SwingUtilities.invokeAndWait(runnable);
		} catch (InvocationTargetException | InterruptedException e) {
			logger.warn("Error updating details", e);
		}
	}

	@Override
	public void contentsChanged(ListDataEvent event) {
		String serverName = (String) model.getServersModel().getSelectedItem();
		String processName = (String) model.getCollectorsModel()
				.getSelectedItem();

		if (serverName == null || serverName.isEmpty()) {
			initialized = false;

			return;
		}

		if (processName == null || processName.isEmpty()) {
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
