package ru.sname.config.worker;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ru.sname.config.service.SiuService;
import ru.sname.config.tree.ConfigNode;

import com.hp.siu.utils.Config;

public abstract class AbstractSuiWorker extends AbstractConfigWorker {

	protected static final String[] PROCESS_CLASS = { ".CollectorProcess",
			".ConfigServerProcess", ".LogConsolidatorProcess",
			".PolicyServerProcess", ".ScheduleServerProcess",
			".SessionServerProcess" };

	protected static final String PROPERTIES_NODE = "Properties";

	protected static final String CLASSNAME_ATTRIBUTE = "ClassName";

	protected static final String JVM_OPTIONS = "JVMOPTS";
	protected static final String JVM_DEBUG = "-Xdebug";
	protected static final String JVM_RUN_JDWP = "-Xrunjdwp";

	protected SiuService service;
	protected String serverName;
	protected String processName;

	protected Collection<ConfigNode> findProcesses(ConfigNode root) {
		Collection<ConfigNode> processes = new LinkedList<ConfigNode>();

		findProcessesReq(root, processes);

		return processes;
	}

	private void findProcessesReq(ConfigNode node,
			Collection<ConfigNode> processes) {
		for (String value : node.getValues(CLASSNAME_ATTRIBUTE)) {
			for (String className : PROCESS_CLASS) {
				if (value.endsWith(className)) {
					processes.add(node);

					return;
				}
			}
		}

		for (ConfigNode child : node.getChildren()) {
			findProcessesReq(child, processes);
		}
	}

	protected Config buildConfig(ConfigNode source) {
		Config destination = new Config(source.getName());

		for (String attribute : source.getAttributes()) {
			List<String> values = source.getValues(attribute);

			destination.setAttributes(attribute,
					values.toArray(new String[] {}));
		}

		for (ConfigNode child : source.getChildren()) {
			String childName = child.getName();
			Config node = buildConfig(child);

			destination.setConfigEntry(childName, node);
		}

		return destination;
	}

	public void setService(SiuService service) {
		this.service = service;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

}
