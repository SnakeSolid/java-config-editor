package ru.snake.config.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.snake.config.dialog.ConnectionDialog;

@SuppressWarnings("serial")
@Component("server_connect_action")
public class ServerConnectAction extends ActionAdapter {

	@Autowired
	private ConnectionDialog dialog;

	public ServerConnectAction() {
		setName("Connect");
		setDescription("Connect to specified server.");
		setMnemonic(KeyEvent.VK_C);
		setAccelerator(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK
				| InputEvent.SHIFT_DOWN_MASK);
		setIcon("connect");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		dialog.setVisible(true);
	}

}
