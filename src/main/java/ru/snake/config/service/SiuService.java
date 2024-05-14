package ru.snake.config.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
import com.hp.siu.corba.StatusType;
import com.hp.siu.utils.ClientException;
import com.hp.siu.utils.CollectorOperationClient;
import com.hp.siu.utils.Config;
import com.hp.siu.utils.ConfigManager;
import com.hp.siu.utils.LoginContext;
import com.hp.siu.utils.ManagedProcessClient;
import com.hp.siu.utils.Measurement;
import com.hp.siu.utils.ProcessManagerClient;
import com.hp.siu.utils.SIUInfo;
import com.hp.siu.utils.SafeFileHandlerClient;
import com.hp.siu.utils.Stats;

import ru.snake.config.util.DetatilsDescriptor;

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
	private Map<String, SafeFileHandlerClient> tailers;
	private Map<String, CollectorOperationClient> collectors;

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

		// Hack, create temporary folder for user store
		File tepmorary = new File(temporaryDirectory);
		File tepmStore = new File(tepmorary, "tmp");

		if (!tepmStore.exists()) {
			tepmStore.mkdir();
		}

		context = new LoginContext(iorUrl);
		context.init(username, password);

		configManager = new ConfigManager(iorUrl, context);

		fireConnected();
	}

	public void disconnect() {
		processes.clear();
		managers.clear();
		tailers.clear();
		collectors.clear();

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
		this.tailers = new HashMap<String, SafeFileHandlerClient>();
		this.collectors = new HashMap<String, CollectorOperationClient>();

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

	public Config getConfigTree(String serverName, String processName)
			throws ClientException {
		StringBuilder path = new StringBuilder();
		path.append("/deployment/");
		path.append(serverName);
		path.append('/');
		path.append(processName);
		path.append('/');

		return configManager.getConfigTree(path.toString(), true);
	}

	public List<String> getServers() throws ClientException {
		StringBuilder path = new StringBuilder();
		path.append("/deployment/");

		Config node = configManager.getConfigEntry(path.toString());
		ArrayList<String> serverNames = new ArrayList<String>();

		@SuppressWarnings("unchecked")
		Enumeration<String> children = node.getConfigNames();

		while (children.hasMoreElements()) {
			String childName = children.nextElement();
			StringBuilder childPath = new StringBuilder();
			childPath.append("/deployment/");
			childPath.append(childName);

			Config child = configManager.getConfigEntry(childPath.toString());

			@SuppressWarnings("unchecked")
			Enumeration<String> attributes = child.getAttributeNames();

			while (attributes.hasMoreElements()) {
				String attributeName = attributes.nextElement();

				if (attributeName.equals("Processes")) {
					serverNames.add(childName);

					break;
				}
			}
		}

		Collections.sort(serverNames);

		return serverNames;
	}

	public List<String> getCollectors(String serverName) throws ClientException {
		StringBuilder path = new StringBuilder();
		path.append("/deployment/");
		path.append(serverName);
		path.append('/');

		Config node = configManager.getConfigEntry(path.toString());
		ArrayList<String> processNames = new ArrayList<String>();
		String[] children = node.getAttributes("Processes");

		for (String childPath : children) {
			int index = childPath.lastIndexOf('/');

			if (index != -1) {
				String childName = childPath.substring(index + 1);

				processNames.add(childName);
			}
		}

		Collections.sort(processNames);

		return processNames;
	}

	public void stopProcess(String serverName, String processName)
			throws ClientException {
		if (isProcessStopped(serverName, processName)) {
			return;
		}

		ManagedProcessClient process = getProcess(serverName, processName);

		if (process != null) {
			process.stopProcess();

			processes.remove(processName);
			collectors.remove(processName);
		}
	}

	public boolean isProcessRunning(String serverName, String processName)
			throws ClientException {
		ManagedProcessClient process = getProcess(serverName, processName);

		if (process == null) {
			return false;
		}

		StatusType status = process.getStatus().status;

		if (status.equals(StatusType.CMDL_NORMAL)) {
			return true;
		} else if (status.equals(StatusType.CMDL_MANUAL)) {
			return true;
		} else if (status.equals(StatusType.CMDL_RESTART)) {
			return true;
		}

		return false;
	}

	public boolean isProcessStopped(String serverName, String processName)
			throws ClientException {
		ManagedProcessClient process = getProcess(serverName, processName);

		if (process == null) {
			return false;
		}

		StatusType status = process.getStatus().status;

		if (status.equals(StatusType.CMDR_NORMAL)) {
			return true;
		} else if (status.equals(StatusType.CMDR_MANUAL)) {
			return true;
		} else if (status.equals(StatusType.CONFIGURED)) {
			return true;
		}

		return false;
	}

	public boolean isProcessCrashed(String serverName, String processName)
			throws ClientException {
		ManagedProcessClient process = getProcess(serverName, processName);

		if (process == null) {
			return false;
		}

		StatusType status = process.getStatus().status;

		if (status.equals(StatusType.CMDR_CRASH)) {
			return true;
		}

		return false;
	}

	public boolean isProcessFailed(String serverName, String processName)
			throws ClientException {
		ManagedProcessClient process = getProcess(serverName, processName);

		if (process == null) {
			return false;
		}

		StatusType status = process.getStatus().status;

		if (status.equals(StatusType.CMDR_LAUNCH_FAIL)) {
			return true;
		}

		return false;
	}

	public void cleanupProcess(String serverName, String processName)
			throws ClientException {
		ManagedProcessClient process = getProcess(serverName, processName);

		if (process != null) {
			process.cleanup();
		}
	}

	public void startProcess(String serverName, String processName)
			throws ClientException {
		if (isProcessRunning(serverName, processName)) {
			return;
		}

		ManagedProcessClient process = getProcess(serverName, processName);

		if (process != null) {
			ArrayList<PropertyInfo> properties = getProcessProperties(
					serverName, processName);
			PropertyList propertyList = new PropertyList(
					properties.toArray(new PropertyInfo[] {}));

			process.startProcess(propertyList);
		}
	}

	private ArrayList<PropertyInfo> getProcessProperties(String serverName,
			String processName) throws ClientException {
		ProcessManagerClient manager = getManager(serverName);
		PropertyList runtimeParams = manager.getRuntimeParams();
		ArrayList<PropertyInfo> properties = new ArrayList<PropertyInfo>();

		String fullName = getProcessPath(serverName, processName);
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
			String processName) throws ClientException {
		ManagedProcessClient process;

		if (processes.containsKey(processName)) {
			process = processes.get(processName);
		} else {
			ProcessManagerClient manager = getManager(serverName);

			process = manager.getProcessByName(processName);

			if (process != null) {
				processes.put(processName, process);
			}
		}

		return process;
	}

	private String getProcessPath(String serverName, String processName) {
		StringBuilder builder = new StringBuilder();
		builder.append("/deployment/");
		builder.append(serverName);
		builder.append("/");
		builder.append(processName);

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

	public void updateProcessConfig(String serverName, String processName,
			Config config) throws ClientException {
		StringBuilder builder = new StringBuilder();
		builder.append("/deployment/");
		builder.append(serverName);

		config.setPathName(builder.toString());
		config.setName(processName);

		configManager.setConfigTree(config);
	}

	public SafeFileHandlerClient createByteTailer(String serverName)
			throws ClientException {
		if (tailers.containsKey(serverName)) {
			return tailers.get(serverName);
		}

		SafeFileHandlerClient handler = new SafeFileHandlerClient(serverName,
				configManager, context);

		tailers.put(serverName, handler);

		return handler;
	}

	private String getVarRoot(String serverName) throws ClientException {
		ProcessManagerClient manager = getManager(serverName);
		PropertyList runtimeParams = manager.getRuntimeParams();

		for (PropertyInfo property : runtimeParams.properties) {
			if (property.key.equals("VARROOT")) {
				return property.value;
			}
		}

		return null;
	}

	public String getLogFileName(String serverName, String processName)
			throws ClientException {
		String varRoot = getVarRoot(serverName);
		StringBuilder builder = new StringBuilder();
		builder.append(varRoot);
		builder.append("/log/");
		builder.append(processName);
		builder.append(".log");

		return builder.toString();
	}

	public void getProcessDetails(String serverName, String processName,
			DetatilsDescriptor descriptor) throws ClientException {
		if (!isProcessRunning(serverName, processName)) {
			return;
		}

		ManagedProcessClient process = getProcess(serverName, processName);

		if (process != null) {
			String type = process.getProcessType();

			descriptor.putDate("Start time", process.getStartTime());
			descriptor.putString("Name", process.getName());
			descriptor.putString("Type", type);
			descriptor.putString("Dependencies", process.getDependencies());
			descriptor.putString("Status description",
					statusToString(process.getStatus().status));
			descriptor.putString("Status reason", process.getStatus().reason);

			if (type.equals("com.hp.siu.adminagent.procmgr.CollectorProcess")) {
				CollectorOperationClient client = getCollector(serverName,
						processName);

				descriptor.putLong("Memoty free", client.getFreeMem());
				descriptor.putLong("Memoty total", client.getTotalMem());
				descriptor.putLong("Log level", client.getLogLevel());

				Stats stats = client.getStatisticsDetail();

				@SuppressWarnings("unchecked")
				Enumeration<String> measurementNames = stats
						.getMeasurementNames();

				while (measurementNames.hasMoreElements()) {
					String measurementName = measurementNames.nextElement();
					Measurement measurement = stats
							.getMeasurement(measurementName);
					long value = measurement.getValue();

					descriptor.putLong(measurementName, value);
				}
			}
		}
	}

	private CollectorOperationClient getCollector(String serverName,
			String processName) {
		CollectorOperationClient client;

		if (collectors.containsKey(processName)) {
			client = collectors.get(processName);
		} else {
			client = new CollectorOperationClient(serverName, processName,
					configManager, context);

			collectors.put(processName, client);
		}

		return client;
	}

	private String statusToString(StatusType status) {
		if (status.equals(StatusType.CMDL_NORMAL)) {
			return "Running";
		} else if (status.equals(StatusType.CMDL_MANUAL)) {
			return "Running";
		} else if (status.equals(StatusType.CMDL_RESTART)) {
			return "Running";
		} else if (status.equals(StatusType.CMDR_NORMAL)) {
			return "Stopped";
		} else if (status.equals(StatusType.CMDR_MANUAL)) {
			return "Stopped";
		} else if (status.equals(StatusType.CONFIGURED)) {
			return "Stopped";
		} else if (status.equals(StatusType.CMDR_CRASH)) {
			return "Crashed";
		} else if (status.equals(StatusType.CMDR_LAUNCH_FAIL)) {
			return "Failed";
		} else {
			return "Unknown";
		}
	}

}
