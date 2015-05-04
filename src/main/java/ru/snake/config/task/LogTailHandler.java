package ru.snake.config.task;

import com.hp.siu.utils.SafeFileHandlerClient;

public interface LogTailHandler {

	public void byteTailerCreated(String serverName, String filename,
			SafeFileHandlerClient fileTailer);

}
