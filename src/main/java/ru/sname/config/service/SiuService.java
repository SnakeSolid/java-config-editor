package ru.sname.config.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.hp.siu.corba.PropertyInfo;
import com.hp.siu.corba.PropertyList;
import com.hp.siu.utils.ClientException;
import com.hp.siu.utils.Config;
import com.hp.siu.utils.ConfigManager;
import com.hp.siu.utils.LoginContext;
import com.hp.siu.utils.ManagedProcessClient;
import com.hp.siu.utils.ProcMgrException;
import com.hp.siu.utils.ProcessInfo;
import com.hp.siu.utils.ProcessList;
import com.hp.siu.utils.ProcessManagerClient;
import com.hp.siu.utils.SIUInfo;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SiuService {

	private static final Logger logger = LoggerFactory
			.getLogger(SiuService.class);

	private String temporaryDirectory;
	private LoginContext context;
	private ConfigManager configManager;

	private Map<String, ProcessManagerClient> managers;
	private Map<String, ManagedProcessClient> processes;

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
		processes.clear();
		managers.clear();

		context = null;
		configManager = null;

		fireDisconnected();
	}

	public boolean isConnected() {
		return context != null && configManager != null;
	}

	@PostConstruct
	private void initialize() {
		this.temporaryDirectory = System.getProperty("java.io.tmpdir");

		this.managers = new HashMap<String, ProcessManagerClient>();
		this.processes = new HashMap<String, ManagedProcessClient>();

		this.listeners = new LinkedList<SiuListener>();
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
		ArrayList<String> serverNames = new ArrayList<String>();

		@SuppressWarnings("unchecked")
		Enumeration<String> children = node.getConfigNames();

		while (children.hasMoreElements()) {
			serverNames.add(children.nextElement());
		}

		Collections.sort(serverNames);

		return serverNames;
	}

	public Collection<String> getCollectors(String server)
			throws ClientException {
		StringBuilder path = new StringBuilder();
		path.append("/deployment/");
		path.append(server);
		path.append('/');

		Config node = configManager.getConfigTree(path.toString());
		ArrayList<String> collectorNames = new ArrayList<String>();

		@SuppressWarnings("unchecked")
		Enumeration<String> children = node.getConfigNames();

		while (children.hasMoreElements()) {
			collectorNames.add(children.nextElement());
		}

		Collections.sort(collectorNames);

		return collectorNames;
	}

	public void stopProcess(String serverName, String collectorName)
			throws ProcMgrException, ClientException {
		if (processExists(serverName, collectorName)) {
			ManagedProcessClient process = getProcess(serverName, collectorName);

			process.stopProcess();

			processes.remove(process);
		}
	}

	public void cleanupProcess(String serverName, String collectorName)
			throws ProcMgrException, ClientException {
		if (processExists(serverName, collectorName)) {
			ManagedProcessClient process = getProcess(serverName, collectorName);

			process.cleanup();
		}
	}

	public void startProcess(String serverName, String collectorName)
			throws ProcMgrException, ClientException {
		if (processExists(serverName, collectorName)) {
			ManagedProcessClient process = getProcess(serverName, collectorName);
			ArrayList<PropertyInfo> properties = getProcessProperties(
					serverName, collectorName);
			PropertyList propertyList = new PropertyList(
					properties.toArray(new PropertyInfo[] {}));

			process.startProcess(propertyList);
		}
	}

	private ArrayList<PropertyInfo> getProcessProperties(String serverName,
			String collectorName) throws ClientException {
		ProcessManagerClient manager = getManager(serverName);
		PropertyList runtimeParams = manager.getRuntimeParams();
		ArrayList<PropertyInfo> properties = new ArrayList<PropertyInfo>();

		String fullName = getProcessPath(serverName, collectorName);
		Config node = configManager.getConfigEntry(fullName);

		if (hasEntry(node, "Properties")) {
			Config propertiesNode = configManager.getConfigEntry(fullName
					+ "/Properties");

			@SuppressWarnings("unchecked")
			Enumeration<String> attributeIt = propertiesNode
					.getAttributeNames();

			while (attributeIt.hasMoreElements()) {
				String attribute = attributeIt.nextElement();

				if (attribute.equals("CLASSPATH")) {
					mergeProcessProperties(propertiesNode, runtimeParams,
							properties, attribute);
				} else if (attribute.equals("JVMOPT")) {
					mergeProcessProperties(propertiesNode, runtimeParams,
							properties, attribute);
				} else if (attribute.equals("JVMPROPERTIES")) {
					mergeProcessProperties(propertiesNode, runtimeParams,
							properties, attribute);
				}
			}
		}

		return properties;
	}

	private void mergeProcessProperties(Config node,
			PropertyList runtimeParams, Collection<PropertyInfo> properties,
			String attribute) {
		for (String value : node.getAttributes(attribute)) {
			if (value.equals("%DEFAULTS%")) {
				for (PropertyInfo info : runtimeParams.properties) {
					if (attribute.equals(info.key)) {
						properties.add(info);
					}
				}
			} else {
				PropertyInfo info = new PropertyInfo(attribute, value);

				properties.add(info);
			}
		}
	}

	private boolean hasEntry(Config node, String string) {
		@SuppressWarnings("unchecked")
		Enumeration<String> nameIt = node.getConfigNames();

		while (nameIt.hasMoreElements()) {
			String childName = nameIt.nextElement();

			if (childName.equals(string)) {
				return true;
			}
		}

		return false;
	}

	private ManagedProcessClient getProcess(String serverName,
			String collectorName) throws ProcMgrException, ClientException {
		ManagedProcessClient process;

		if (processes.containsKey(collectorName)) {
			process = processes.get(collectorName);
		} else {
			ProcessManagerClient manager = getManager(serverName);

			process = manager.getProcessByName(collectorName);

			processes.put(collectorName, process);
		}

		return process;
	}

	private boolean processExists(String serverName, String collectorName)
			throws ProcMgrException, ClientException {
		String fullName = getProcessPath(serverName, collectorName);
		ProcessManagerClient manager = getManager(serverName);
		ProcessList list = manager.getProcessList();

		@SuppressWarnings("unchecked")
		Enumeration<ProcessInfo> it = list.getProcesses();

		while (it.hasMoreElements()) {
			ProcessInfo processInfo = it.nextElement();
			String processName = processInfo.getName();

			if (fullName.equals(processName)) {
				return true;
			}
		}

		processes.keySet().remove(collectorName);

		return false;
	}

	private String getProcessPath(String serverName, String collectorName) {
		StringBuilder builder = new StringBuilder();
		builder.append("/deployment/");
		builder.append(serverName);
		builder.append("/");
		builder.append(collectorName);

		return builder.toString();
	}

	private ProcessManagerClient getManager(String serverName) {
		ProcessManagerClient manager;

		if (managers.containsKey(serverName)) {
			manager = managers.get(serverName);
		} else {
			manager = new ProcessManagerClient(serverName, configManager,
					context);

			managers.put(serverName, manager);
		}

		return manager;
	}

	public void updateProcessConfig(String serverName, String collectorName,
			Config node) {
		// TODO Auto-generated method stub

	}

}
