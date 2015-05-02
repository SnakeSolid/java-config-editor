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
@Component("configuration_load_action")
public class ConfigurationLoadAction extends ActionAdapter {

	@Autowired
	private ConfigModel model;

	@Autowired
	private SiuService siuService;

	@Autowired
	private WorkerExecutor executor;

	public ConfigurationLoadAction() {
		setName("Load");
		setDescription("Load selected collector configuration.");
		setMnemonic(KeyEvent.VK_L);
		setAccelerator(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK
				| InputEvent.SHIFT_DOWN_MASK);
		setIcon("download");
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

		executor.executeLoadConfig(serverName, collectorName);
	}
}
