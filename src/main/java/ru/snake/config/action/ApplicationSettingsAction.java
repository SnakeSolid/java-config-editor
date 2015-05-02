package ru.snake.config.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.snake.config.dialog.SettingsDialog;

@SuppressWarnings("serial")
@Component("application_settings_action")
public class ApplicationSettingsAction extends ActionAdapter {

	@Autowired
	private SettingsDialog dialog;

	public ApplicationSettingsAction() {
		setName("Settings...");
		setDescription("Open settings dialog.");
		setMnemonic(KeyEvent.VK_E);
		setAccelerator(KeyEvent.VK_P, InputEvent.SHIFT_DOWN_MASK
				| InputEvent.CTRL_DOWN_MASK);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		dialog.setVisible(true);
	}

}
