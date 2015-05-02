package ru.sname.config.task;

import com.hp.siu.utils.SafeFileHandlerClient;

public interface LogTailHandler {

	public void byteTailerCreated(String serverName,
			SafeFileHandlerClient fileTailer);

}
