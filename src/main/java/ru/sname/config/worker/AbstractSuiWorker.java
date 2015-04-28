package ru.sname.config.worker;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ru.sname.config.service.SiuService;
import ru.sname.config.tree.ConfigNode;

import com.hp.siu.utils.Config;

public abstract class AbstractSuiWorker extends AbstractConfigWorker  {

	protected SiuService service;
	protected String serverName;
	protected String collectorName;

	protected Collection<ConfigNode> findProcesses(ConfigNode root) {
		Collection<ConfigNode> processes = new LinkedList<ConfigNode>();

		findProcessesReq(root, processes);

		return processes;
	}

	private void findProcessesReq(ConfigNode node,
			Collection<ConfigNode> processes) {
		for (String value : node.getValues("ClassName")) {
			if (value.endsWith(".CollectorProcess")) {
				processes.add(node);

				return;
			} else if (value.endsWith(".ConfigServerProcess")) {
				processes.add(node);

				return;
			} else if (value.endsWith(".LogConsolidatorProcess")) {
				processes.add(node);

				return;
			} else if (value.endsWith(".PolicyServerProcess")) {
				processes.add(node);

				return;
			} else if (value.endsWith(".ScheduleServerProcess")) {
				processes.add(node);

				return;
			} else if (value.endsWith(".SessionServerProcess")) {
				processes.add(node);

				return;
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

	public void setServer(String serverName) {
		this.serverName = serverName;
	}

	public void setCollector(String collectorName) {
		this.collectorName = collectorName;
	}

}
