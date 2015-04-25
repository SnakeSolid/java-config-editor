package ru.sname.config.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.sname.config.service.SiuService;

@SuppressWarnings("serial")
@Component("server_disconnect_action")
public class ServerDisconnectAction extends ActionAdapter {

	@Autowired
	private SiuService service;

	public ServerDisconnectAction() {
		setName("Disconnect");
		setDescription("Disconnect to specified server.");
		setMnemonic(KeyEvent.VK_D);
		setAccelerator(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK
				| InputEvent.SHIFT_DOWN_MASK);
		setIcon("snake");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (service.isConnected()) {
			service.disconnect();
		}
	}

}
