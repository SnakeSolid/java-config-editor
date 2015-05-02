package ru.snake.config.worker;

import ru.snake.config.model.SettingsModel;

public class SaveSettingsWorker extends AbstractConfigWorker {

	private SettingsModel settings;

	public void setSettings(SettingsModel settings) {
		this.settings = settings;
	}

	@Override
	protected Void doInBackground() throws Exception {
		info("Saving settings...");

		settings.save();

		info("Settings has been saved.");

		return null;
	}

}
