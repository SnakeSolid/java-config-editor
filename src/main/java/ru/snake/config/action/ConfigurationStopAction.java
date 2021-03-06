package ru.snake.config.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.snake.config.model.ConfigModel;
import ru.snake.config.model.StringBoxModel;
import ru.snake.config.service.SiuService;
import ru.snake.config.service.WorkerExecutor;

@SuppressWarnings("serial")
@Component("configuration_stop_action")
public class ConfigurationStopAction extends ActionAdapter {

	@Autowired
	private ConfigModel model;

	@Autowired
	private SiuService siuService;

	@Autowired
	private WorkerExecutor executor;

	public ConfigurationStopAction() {
		setName("Stop");
		setDescription("Stop selected collector.");
		setMnemonic(KeyEvent.VK_S);
		setAccelerator(KeyEvent.VK_F2, InputEvent.CTRL_DOWN_MASK);
		setIcon("stop");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (!siuService.isConnected()) {
			JOptionPane.showMessageDialog(null, "SIU does not connected.",
					"Communication error", JOptionPane.WARNING_MESSAGE);

			return;
		}

		StringBoxModel servers = model.getServersModel();
		String serverName = (String) servers.getSelectedItem();
		StringBoxModel collectors = model.getCollectorsModel();
		String collectorName = (String) collectors.getSelectedItem();

		if (collectorName == null) {
			JOptionPane.showMessageDialog(null, "Collector does not chosen.",
					"Communication error", JOptionPane.WARNING_MESSAGE);

			return;
		}

		executor.executeStopProcess(serverName, collectorName);
	}

}
