package ru.snake.config.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.text.JTextComponent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.snake.config.dialog.FindDialog;

@SuppressWarnings("serial")
@Component("edit_find_action")
public class EditFindAction extends ActionAdapter {

	@Autowired
	private FindDialog dialog;

	public EditFindAction() {
		setName("Find...");
		setDescription("Find in current confuguration.");
		setMnemonic(KeyEvent.VK_F);
		setAccelerator(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();

		if (source instanceof JTextComponent) {
			JTextComponent textComponent = (JTextComponent) source;
			String selected = textComponent.getSelectedText();

			if (selected != null) {
				dialog.setFindText(selected);
			}

			dialog.setSource(textComponent);
			dialog.setVisible(true);
		}
	}

}
