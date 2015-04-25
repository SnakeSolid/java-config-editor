package ru.sname.config.listener;

import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.StyledDocument;

import ru.sname.config.worker.HighlightWorker;

public class DocumentHighlightListener implements DocumentListener {

	private SwingWorker<Void, Void> task;

	public DocumentHighlightListener() {
		this.task = null;
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		StyledDocument document = (StyledDocument) e.getDocument();

		startWorker(document, e.getOffset(), e.getLength());
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		StyledDocument document = (StyledDocument) e.getDocument();

		startWorker(document, e.getOffset(), e.getLength());
	}

	private void startWorker(StyledDocument document, int offset, int length) {
		if (task != null) {
			if (!task.isDone()) {
				task.cancel(true);
			}
		}

		task = new HighlightWorker(document, offset, length);
		task.execute();
	}

}
