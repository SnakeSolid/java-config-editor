package ru.sname.config.action;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.springframework.stereotype.Component;

@Component("configuration_debug_action")
public class ConfigurationDebugAction extends ActionAdapter {

	private static final long serialVersionUID = 8622036338103433498L;

	public ConfigurationDebugAction() {
		setName("Debug");
		setDescription("Debug current confuguration as selected collector.");
		setMnemonic(KeyEvent.VK_D);
		setAccelerator(KeyEvent.VK_F11);
		setIcon("bug");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JComponent source = (JComponent) event.getSource();
		Container ancestor = source.getTopLevelAncestor();

		JOptionPane.showMessageDialog(ancestor, "message");
	}

}
