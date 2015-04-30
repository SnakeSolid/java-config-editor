package ru.sname.config.worker;

import ru.sname.config.service.SiuService;

import com.hp.siu.utils.ClientException;

public class ConnectWorker extends AbstractConfigWorker {

	private SiuService service;
	private String ior;
	private boolean anonymous;
	private String username;
	private String password;

	public void setService(SiuService service) {
		this.service = service;
	}

	public void setIorUrl(String ior) {
		this.ior = ior;
	}

	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	protected Void doInBackground() throws Exception {
		append("Connecting to {0}...", ior);

		try {
			if (anonymous) {
				service.connect(ior);
			} else {
				service.connect(ior, username, password);
			}
		} catch (ClientException e) {
			append("Connection error, caused by: {0}", e.getMessage());

			return null;
		}

		append("Successfully connected.");

		return null;
	}

}
