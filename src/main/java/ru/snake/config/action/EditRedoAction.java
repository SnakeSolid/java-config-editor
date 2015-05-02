package ru.snake.config.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.undo.UndoManager;

@SuppressWarnings("serial")
public class EditRedoAction extends ActionAdapter {

	private final UndoManager undoManager;

	public EditRedoAction(UndoManager undoManager) {
		this.undoManager = undoManager;

		setName("Redo");
		setDescription("Redo last edit.");
		setMnemonic(KeyEvent.VK_R);
		setAccelerator(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK
				| InputEvent.SHIFT_DOWN_MASK);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (undoManager.canRedo()) {
			undoManager.redo();
		}
	}

}
