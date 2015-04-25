package ru.sname.config.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.springframework.stereotype.Component;

@Component("configuration_debug_action")
public class ConfigurationDebugAction extends ActionAdapter {

	private static final long serialVersionUID = 8622036338103433498L;

	public ConfigurationDebugAction() {
		setName("Debug");
		setDescription("Debug current confuguration as selected collector.");
		setIcon("bug");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JOptionPane.showMessageDialog(null, "message");
	}

}
