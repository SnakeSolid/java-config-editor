package ru.sname.config.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.springframework.stereotype.Component;

@SuppressWarnings("serial")
@Component("configuration_stop_action")
public class ConfigurationStopAction extends ActionAdapter {

	public ConfigurationStopAction() {
		setName("Stop");
		setDescription("Stop selected collector.");
		setIcon("owl");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JOptionPane.showMessageDialog(null, "message");
	}

}
