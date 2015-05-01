package ru.sname.config.util;

import javax.swing.JOptionPane;

public class MessageDisposer implements Runnable {

	private final String message;

	public MessageDisposer(String message) {
		this.message = message;
	}

	@Override
	public void run() {
		JOptionPane.showMessageDialog(null, message);
	}

}
