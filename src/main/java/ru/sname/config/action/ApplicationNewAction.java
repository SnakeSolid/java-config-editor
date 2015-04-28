package ru.sname.config.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.annotation.PostConstruct;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.sname.config.model.ConfigModel;

@SuppressWarnings("serial")
@Component("application_new_action")
public class ApplicationNewAction extends ActionAdapter {

	@Autowired
	private ConfigModel model;

	@PostConstruct
	private void initialize() {
		setName("New");
		setDescription("Create new configuration.");
		setMnemonic(KeyEvent.VK_N);
		setAccelerator(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		StyledDocument doc = model.getConfigurationModel();
		int length = doc.getLength();

		if (length > 0) {
			int result = JOptionPane.showOptionDialog(null,
					"Do you want to clear current configuration?", null,
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
					null, null, null);

			if (result == JOptionPane.YES_OPTION) {
				clearDocument(doc, length);
			}
		} else {
			clearDocument(doc, length);
		}
	}

	private void clearDocument(StyledDocument doc, int length) {
		try {
			doc.remove(0, length);
		} catch (BadLocationException e) {
		}
	}

}
