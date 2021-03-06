package ru.snake.config.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.annotation.PostConstruct;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.snake.config.model.ConfigModel;
import ru.snake.config.service.WorkerExecutor;

@Component("application_open_action")
public class ApplicationOpenAction extends ActionAdapter {

	private static final long serialVersionUID = 3930722524574213888L;

	@Autowired
	private ConfigModel model;

	@Autowired
	private WorkerExecutor executor;

	@PostConstruct
	private void initialize() {
		setName("Open...");
		setDescription("Open configuration.");
		setMnemonic(KeyEvent.VK_O);
		setAccelerator(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		FileFilter configFilter = new FileNameExtensionFilter(
				"SIU configuration file (*.config)", "config", "conf", "cfg");
		JFileChooser chooser = new JFileChooser(model.getWorkingDirectory());
		chooser.setFileFilter(configFilter);
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();

			if (file.canRead()) {
				executor.executeLoadFile(file);
			}

			model.setWorkingDirectory(file.getParentFile());
			model.setWorkingFile(file);
		}
	}

}
