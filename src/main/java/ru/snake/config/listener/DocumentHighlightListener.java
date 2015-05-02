package ru.snake.config.listener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.StyledDocument;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.snake.config.service.WorkerExecutor;

@Component("document_highlight_listener")
public class DocumentHighlightListener implements DocumentListener {

	@Autowired
	private WorkerExecutor executor;

	@Override
	public void changedUpdate(DocumentEvent e) {
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		StyledDocument document = (StyledDocument) e.getDocument();

		executor.executeHighlight(document, e.getOffset(), e.getLength());
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		StyledDocument document = (StyledDocument) e.getDocument();

		executor.executeHighlight(document, e.getOffset(), e.getLength());
	}

}
