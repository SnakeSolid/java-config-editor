package ru.snake.config.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.snake.config.model.ConfigModel;
import ru.snake.config.service.WorkerExecutor;

@SuppressWarnings("serial")
@Component("application_save_action")
public class ApplicationSaveAction extends AbstractSaveAction {

	@Autowired
	private ConfigModel model;

	@Autowired
	private WorkerExecutor executor;

	@PostConstruct
	private void initialize() {
		setName("Save");
		setDescription("Save current configuration.");
		setMnemonic(KeyEvent.VK_S);
		setAccelerator(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		File file = model.getWorkingFile();

		if (file == null) {
			saveConfigAs(model, executor);
		} else {
			executor.executeSaveFile(file);
		}
	}

}
