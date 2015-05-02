package ru.sname.config.worker;

import java.util.Collection;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.sname.config.tree.ConfigNode;
import ru.sname.config.tree.TreeParser;

import com.hp.siu.utils.ClientException;
import com.hp.siu.utils.Config;

public class DebugProcessWorker extends AbstractSuiWorker {

	private static final Logger logger = LoggerFactory
			.getLogger(DebugProcessWorker.class);

	private String content;

	@Override
	protected Void doInBackground() {
		info("Stopping process {0}...", processName);

		try {
			service.stopProcess(serverName, processName);
		} catch (ClientException e) {
			info("Failed to stop process {0}, caused by: {1}.", processName,
					e.getMessage());
			logger.warn("Failed to stop process", e);
		}

		info("Process {0} has been stopped.", processName);
		info("Cleaning process {0}...", processName);

		try {
			service.cleanupProcess(serverName, processName);
		} catch (ClientException e) {
			warn("Failed to clearup process {0}, caused by: {1}.", processName,
					e.getMessage());
			logger.warn("Can not cleanup process", e);
		}

		info("Process has been cleaned.");
		info("Updating process {0} configuration...", processName);

		ConfigNode root = TreeParser.parse(content);
		Collection<ConfigNode> processes = findProcesses(root);

		if (processes.isEmpty()) {
			warn("No one process with name {0} found in config.", processName);
			logger.info("No one process found in config.");

			return null;
		}

		if (processes.size() > 1) {
			warn("More than one process with name {0} found in config.",
					processName);
			logger.warn("More than one process found in config.");

			return null;
		}

		ConfigNode source = processes.iterator().next();

		setDebugParameters(source);

		Config config = buildConfig(source);

		try {
			service.updateProcessConfig(serverName, processName, config);
		} catch (ClientException e) {
			warn("Can not update process {0} configuration, caused by: {1}.",
					processName, e.getMessage());
			logger.warn("Can not update process configuration.", e);

			return null;
		}

		info("Configuration has been updated.");
		info("Starting process {0}...", processName);

		try {
			service.startProcess(serverName, processName);
		} catch (ClientException e) {
			warn("Can not start process {0}, caused by: {1}.", processName,
					e.getMessage());
			logger.warn("Can not start process.", e);

			return null;
		}

		info("Process started successfully.");

		return null;
	}

	private void setDebugParameters(ConfigNode node) {
		ConfigNode properties;

		if (node.hasChild(PROPERTIES_NODE)) {
			properties = node.getChild(PROPERTIES_NODE);
		} else {
			properties = new ConfigNode(PROPERTIES_NODE);

			node.addChild(properties);
		}

		StringBuilder builder = new StringBuilder(64);
		builder.append(JVM_RUN_JDWP);
		builder.append(":server=y,transport=dt_socket,address=");
		builder.append(8000);
		builder.append(",suspend=");
		builder.append('n');

		if (properties.hasAttribute(JVM_OPTIONS)) {
			boolean debugFound = false;
			boolean runjdwpFound = false;

			for (String value : properties.getValues(JVM_OPTIONS)) {
				if (value.equals(JVM_DEBUG)) {
					debugFound = true;
				} else if (value.startsWith(JVM_RUN_JDWP)) {
					runjdwpFound = true;
				}
			}

			if (!debugFound) {
				properties.pushValue(JVM_OPTIONS, JVM_DEBUG);
			}

			if (!runjdwpFound) {
				properties.pushValue(JVM_OPTIONS, builder.toString());
			}
		} else {
			properties.pushValue(JVM_OPTIONS, JVM_DEBUG);
			properties.pushValue(JVM_OPTIONS, builder.toString());
		}
	}

	private String getContent(StyledDocument document) {
		try {
			return document.getText(0, document.getLength());
		} catch (BadLocationException e) {
			logger.error(e.getMessage(), e);

			return "";
		}
	}

	public void setDocument(StyledDocument document) {
		this.content = getContent(document);
	}

}
