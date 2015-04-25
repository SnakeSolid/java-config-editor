package ru.sname.config.service;

import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.hp.siu.utils.ClientException;
import com.hp.siu.utils.Config;
import com.hp.siu.utils.ConfigManager;
import com.hp.siu.utils.LoginContext;
import com.hp.siu.utils.SIUInfo;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SiuService {

	private static final Logger logger = LoggerFactory
			.getLogger(SiuService.class);

	private String temporaryDirectory;
	private LoginContext context;
	private ConfigManager configManager;

	private Collection<SiuListener> listeners;

	public void connect(String iorUrl) throws ClientException {
		logger.info("connect to {}", iorUrl);

		if (iorUrl == null) {
			throw new NullPointerException("IOR Url can not be null.");
		}

		SIUInfo info = SIUInfo.getInstance();

		info.setBinRoot(temporaryDirectory);
		info.setCfgRoot(temporaryDirectory);
		info.setIORURL(iorUrl);
		info.setVarRoot(temporaryDirectory);

		context = new LoginContext(iorUrl);
		context.init();

		configManager = new ConfigManager(iorUrl, context);

		// Hack to establish connection
		configManager.getConfigTree("/");

		fireConnected();
	}

	private void fireConnected() {
		for (SiuListener listener : listeners) {
			listener.onConnected();
		}
	}

	private void fireDisconnected() {
		for (SiuListener listener : listeners) {
			listener.onDisconnected();
		}
	}

	public void connect(String iorUrl, String username, String password)
			throws ClientException {
		logger.info("connect to {} as {}", iorUrl, username);

		if (iorUrl == null) {
			throw new NullPointerException("IOR Url can not be null.");
		}

		if (username == null) {
			throw new NullPointerException("Username can not be null.");
		}

		if (password == null) {
			throw new NullPointerException("Password can not be null.");
		}

		SIUInfo info = SIUInfo.getInstance();

		info.setBinRoot(temporaryDirectory);
		info.setCfgRoot(temporaryDirectory);
		info.setIORURL(iorUrl);
		info.setVarRoot(temporaryDirectory);

		context = new LoginContext(iorUrl);
		context.init(username, password);

		configManager = new ConfigManager(iorUrl, context);

		// Hack to establish connection
		configManager.getConfigTree("/");

		fireConnected();
	}

	public void disconnect() {
		context = null;
		configManager = null;

		fireDisconnected();
	}

	public boolean isConnected() {
		return context != null && configManager != null;
	}

	@PostConstruct
	private void initialize() {
		temporaryDirectory = System.getProperty("java.io.tmpdir");
		listeners = new LinkedList<SiuListener>();
	}

	@PreDestroy
	private void deinitialize() {
		if (isConnected()) {
			disconnect();
		}
	}

	public void addListener(SiuListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeListener(SiuListener listener) {
		listeners.remove(listener);
	}

	public Config getConfigTree(String server, String collector)
			throws ClientException {
		StringBuilder path = new StringBuilder();
		path.append("/deployment/");
		path.append(server);
		path.append('/');
		path.append(collector);
		path.append('/');

		return configManager.getConfigTree(path.toString(), true);
	}

	public Collection<String> getServers() throws ClientException {
		StringBuilder path = new StringBuilder();
		path.append("/deployment/");

		Config node = configManager.getConfigTree(path.toString());
		Collection<String> serverNames = new LinkedList<String>();

		@SuppressWarnings("unchecked")
		Enumeration<String> children = node.getConfigNames();

		while (children.hasMoreElements()) {
			serverNames.add(children.nextElement());
		}

		return serverNames;
	}

	public Collection<String> getCollectors(String server)
			throws ClientException {
		StringBuilder path = new StringBuilder();
		path.append("/deployment/");
		path.append(server);
		path.append('/');

		Config node = configManager.getConfigTree(path.toString());
		Collection<String> serverNames = new LinkedList<String>();

		@SuppressWarnings("unchecked")
		Enumeration<String> children = node.getConfigNames();

		while (children.hasMoreElements()) {
			serverNames.add(children.nextElement());
		}

		return serverNames;
	}

}
