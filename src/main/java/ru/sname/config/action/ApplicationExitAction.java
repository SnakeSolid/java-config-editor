package ru.sname.config.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.annotation.PostConstruct;
import javax.swing.JOptionPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.sname.config.Application;
import ru.sname.config.model.ConfigModel;

@Component("application_exit_action")
public class ApplicationExitAction extends ActionAdapter {

	private static final long serialVersionUID = 3930722524574213888L;

	@Autowired
	private ConfigModel model;

	@PostConstruct
	private void initialize() {
		setName("Exit");
		setDescription("Exit application.");
		setMnemonic(KeyEvent.VK_X);
		setAccelerator(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		int result = JOptionPane.showOptionDialog(null,
				"Do you want to exit application?", null,
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				null, null);

		if (result == JOptionPane.YES_OPTION) {
			Application.exit();
		}
	}

}
