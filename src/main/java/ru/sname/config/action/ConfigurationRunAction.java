package ru.sname.config.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.sname.config.model.ConfigModel;
import ru.sname.config.model.StringBoxModel;
import ru.sname.config.service.SiuService;
import ru.sname.config.service.WorkerExecutor;

@SuppressWarnings("serial")
@Component("configuration_run_action")
public class ConfigurationRunAction extends ActionAdapter {

	@Autowired
	private ConfigModel model;

	@Autowired
	private SiuService siuService;

	@Autowired
	private WorkerExecutor executor;

	public ConfigurationRunAction() {
		setName("Run");
		setDescription("Run current confuguration as selected collector.");
		setMnemonic(KeyEvent.VK_R);
		setAccelerator(KeyEvent.VK_F11, InputEvent.CTRL_DOWN_MASK);
		setIcon("squirrel");
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

		executor.executeRunProcess(serverName, collectorName);
	}

}
