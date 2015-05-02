package ru.snake.config.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.undo.UndoManager;

@SuppressWarnings("serial")
public class EditUndoAction extends ActionAdapter {

	private final UndoManager undoManager;

	public EditUndoAction(UndoManager undoManager) {
		this.undoManager = undoManager;

		setName("Redo");
		setDescription("Undo last edit.");
		setMnemonic(KeyEvent.VK_R);
		setAccelerator(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (undoManager.canUndo()) {
			undoManager.undo();
		}
	}

}
