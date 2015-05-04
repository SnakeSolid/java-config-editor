package ru.snake.config.action;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import ru.snake.config.model.ConfigModel;
import ru.snake.config.service.WorkerExecutor;

@SuppressWarnings("serial")
public abstract class AbstractSaveAction extends ActionAdapter {

	protected void saveConfigAs(ConfigModel model, WorkerExecutor executor) {
		FileFilter configFilter = new FileNameExtensionFilter(
				"SIU configuration file (*.config)", "config", "conf", "cfg");
		JFileChooser chooser = new JFileChooser(model.getWorkingDirectory());
		chooser.setFileFilter(configFilter);
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();

			if (file.exists()) {
				int result = JOptionPane.showConfirmDialog(null,
						"File exists, overwrite?", null,
						JOptionPane.YES_NO_OPTION);

				if (result == JOptionPane.YES_OPTION) {
					if (file.canWrite()) {
						executor.executeSaveFile(file);
					} else {
						JOptionPane.showMessageDialog(null,
								"Can not write file.", null,
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					model.setWorkingDirectory(file.getParentFile());

					return;
				}
			} else {
				String fileName = file.getName();

				if (fileName.indexOf('.') == -1) {
					file = new File(file.getParentFile(), fileName + ".config");
				}

				executor.executeSaveFile(file);
			}

			model.setWorkingDirectory(file.getParentFile());
			model.setWorkingFile(file);
		}
	}

}
