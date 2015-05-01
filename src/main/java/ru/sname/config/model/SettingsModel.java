package ru.sname.config.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SettingsModel {

	private static final Logger logger = LoggerFactory
			.getLogger(SettingsModel.class);

	private static final String CONFIGURATION_CONNENT = "Settings file";
	private static final String CONFIGURATION_FILENAME = "application.config";

	private static final String DEBUG_PORT_KEY = "debug-port";
	private static final String DEBUG_SUSPEND_KEY = "debug-suspend";
	private static final String SERVER_IOR_KEY = "server-ior";
	private static final String SERVER_ANONYMOUS_KEY = "server-anonymous";
	private static final String SERVER_USERNAME_KEY = "server-username";

	private Properties properties;

	@PostConstruct
	private void initialize() {
		properties = new Properties(getDefaults());

		try (FileInputStream in = new FileInputStream(CONFIGURATION_FILENAME)) {
			properties.load(in);
		} catch (IOException e) {
			logger.warn("Settings file not found", e);
		}
	}

	private Properties getDefaults() {
		Properties defaults = new Properties();
		defaults.put(DEBUG_PORT_KEY, "8000");
		defaults.put(DEBUG_SUSPEND_KEY, "true");
		defaults.put(SERVER_ANONYMOUS_KEY, "true");

		return defaults;
	}

	public void save() {
		try (FileOutputStream out = new FileOutputStream(CONFIGURATION_FILENAME)) {
			properties.store(out, CONFIGURATION_CONNENT);
		} catch (IOException e) {
			logger.warn("Failed to save settings", e);
		}
	}

	public int getDebugPort() {
		String value = properties.getProperty(DEBUG_PORT_KEY);

		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			logger.warn("Failed to parse debug port", e);
		}

		return 8000;
	}

	public boolean getDebugSuspend() {
		String value = properties.getProperty(DEBUG_SUSPEND_KEY);

		return Boolean.parseBoolean(value);
	}

	public void setDebugPort(int port) {
		properties.put(DEBUG_PORT_KEY, String.valueOf(port));
	}

	public void setDebugSuspend(boolean suspend) {
		properties.put(DEBUG_SUSPEND_KEY, String.valueOf(suspend));
	}

	public String getSeverIor() {
		String value = properties.getProperty(SERVER_IOR_KEY, "");

		return value;
	}

	public boolean getServerAnonymous() {
		String value = properties.getProperty(SERVER_ANONYMOUS_KEY);

		return Boolean.parseBoolean(value);
	}

	public String getServerUsername() {
		String value = properties.getProperty(SERVER_USERNAME_KEY, "");

		return value;
	}

	public void setSeverIor(String ior) {
		properties.put(SERVER_IOR_KEY, ior);
	}

	public void setServerAnonymous(boolean anonymous) {
		properties.put(SERVER_ANONYMOUS_KEY, String.valueOf(anonymous));
	}

	public void setServerUsername(String username) {
		properties.put(SERVER_USERNAME_KEY, username);
	}

}
