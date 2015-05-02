package ru.snake.config.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.annotation.PostConstruct;
import javax.swing.JFileChooser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.snake.config.model.ConfigModel;
import ru.snake.config.service.WorkerExecutor;

@Component("application_save_action")
public class ApplicationSaveAction extends ActionAdapter {

	private static final long serialVersionUID = 3930722524574213888L;

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
			JFileChooser chooser = new JFileChooser(model.getWorkingDirectory());
			chooser.setMultiSelectionEnabled(false);
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

			if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				file = chooser.getSelectedFile();

				if (!file.exists() || file.canWrite()) {
					executor.executeSaveFile(file);
				}

				model.setWorkingDirectory(file.getParentFile());
				model.setWorkingFile(file);
			}
		} else {
			executor.executeSaveFile(file);
		}
	}

}
