package ru.snake.config.listener;

import javax.swing.UIManager;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

public class FilterUndoListener implements UndoableEditListener {

	private final String changeAction;
	private final UndoManager undoManager;

	public FilterUndoListener(UndoManager undoManager) {
		this.undoManager = undoManager;

		changeAction = UIManager.getString("AbstractDocument.styleChangeText");
	}

	@Override
	public void undoableEditHappened(UndoableEditEvent event) {
		UndoableEdit edit = event.getEdit();
		String name = edit.getPresentationName();

		if (name.equals(changeAction)) {
			return;
		}

		undoManager.addEdit(edit);
	}

}
