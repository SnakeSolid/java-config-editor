package ru.snake.config.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.snake.config.model.ConfigModel;
import ru.snake.config.service.WorkerExecutor;

@SuppressWarnings("serial")
@Component("application_saveas_action")
public class ApplicationSaveAsAction extends AbstractSaveAction {

	@Autowired
	private ConfigModel model;

	@Autowired
	private WorkerExecutor executor;

	@PostConstruct
	private void initialize() {
		setName("Save as ...");
		setDescription("Save current configuration as.");
		setMnemonic(KeyEvent.VK_A);
		setAccelerator(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK
				| InputEvent.SHIFT_DOWN_MASK);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		saveConfigAs(model, executor);
	}

}
