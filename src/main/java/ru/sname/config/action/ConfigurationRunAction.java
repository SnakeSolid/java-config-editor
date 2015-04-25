package ru.sname.config.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

import org.springframework.stereotype.Component;

@Component("configuration_run_action")
public class ConfigurationRunAction extends ActionAdapter {

	private static final long serialVersionUID = 2660090902078917136L;

	public ConfigurationRunAction() {
		setName("Run");
		setDescription("Run current confuguration as selected collector.");
		setMnemonic(KeyEvent.VK_R);
		setAccelerator(KeyEvent.VK_F11, InputEvent.CTRL_DOWN_MASK);
		setIcon("squirrel");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JOptionPane.showMessageDialog(null, "message");
	}

}
