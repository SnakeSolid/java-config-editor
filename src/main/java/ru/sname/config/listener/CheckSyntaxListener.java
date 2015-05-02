package ru.sname.config.listener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.sname.config.service.WorkerExecutor;

@Component("check_syntax_listener")
public class CheckSyntaxListener implements DocumentListener {

	@Autowired
	private WorkerExecutor executor;

	@Override
	public void changedUpdate(DocumentEvent e) {
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		executor.executeCheckSyntax();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		executor.executeCheckSyntax();
	}

}
