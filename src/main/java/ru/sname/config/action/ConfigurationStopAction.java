package ru.sname.config.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

import org.springframework.stereotype.Component;

@SuppressWarnings("serial")
@Component("configuration_stop_action")
public class ConfigurationStopAction extends ActionAdapter {

	public ConfigurationStopAction() {
		setName("Stop");
		setDescription("Stop selected collector.");
		setMnemonic(KeyEvent.VK_S);
		setAccelerator(KeyEvent.VK_F2, InputEvent.CTRL_DOWN_MASK);
		setIcon("owl");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JOptionPane.showMessageDialog(null, "message");
	}

}
