package ru.sname.config.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.sname.config.model.ConfigModel;
import ru.sname.config.model.StringBoxModel;
import ru.sname.config.service.SiuService;
import ru.sname.config.worker.DebugProcessWorker;

@SuppressWarnings("serial")
@Component("configuration_debug_action")
public class ConfigurationDebugAction extends ActionAdapter {

	@Autowired
	private ConfigModel model;

	@Autowired
	private SiuService siuService;

	public ConfigurationDebugAction() {
		setName("Debug");
		setDescription("Debug current confuguration as selected collector.");
		setMnemonic(KeyEvent.VK_D);
		setAccelerator(KeyEvent.VK_F11);
		setIcon("bug");
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

		DebugProcessWorker worker = new DebugProcessWorker();
		worker.setService(siuService);
		worker.setServer(serverName);
		worker.setCollector(collectorName);
		worker.setDocument(model.getConfigurationModel());
		worker.setStatusDocument(model.getStatusModel());
		worker.execute();
	}

}
