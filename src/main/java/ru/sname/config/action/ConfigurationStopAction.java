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

import com.hp.siu.utils.ClientException;

@SuppressWarnings("serial")
@Component("configuration_stop_action")
public class ConfigurationStopAction extends ActionAdapter {

	@Autowired
	private ConfigModel model;

	@Autowired
	private SiuService siuService;

	public ConfigurationStopAction() {
		setName("Stop");
		setDescription("Stop selected collector.");
		setMnemonic(KeyEvent.VK_S);
		setAccelerator(KeyEvent.VK_F2, InputEvent.CTRL_DOWN_MASK);
		setIcon("owl");
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

		try {
			siuService.stopProcess(serverName, collectorName);
		} catch (ClientException e) {
			StringBuilder builder = new StringBuilder();
			builder.append(e.getMessage());

			Throwable cause = e.getCause();

			while (cause != null) {
				builder.append('\n');
				builder.append(cause.getMessage());

				cause = cause.getCause();
			}

			JOptionPane.showMessageDialog(null, builder.toString(),
					"Communication error", JOptionPane.WARNING_MESSAGE);
		}
	}

}
