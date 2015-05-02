package ru.snake.config.listener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.snake.config.service.WorkerExecutor;

@Component("update_tree_listener")
public class UpdateTreeListener implements DocumentListener {

	@Autowired
	private WorkerExecutor executor;

	@Override
	public void changedUpdate(DocumentEvent e) {
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		executor.executeTreeBuilder();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		executor.executeTreeBuilder();
	}

}
