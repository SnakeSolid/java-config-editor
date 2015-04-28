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

public class StartProcessWorker extends AbstractSuiWorker {

	private static final Logger logger = LoggerFactory
			.getLogger(StartProcessWorker.class);

	private String content;

	@Override
	protected Void doInBackground() throws Exception {
		append("Stopping collector {0}...", collectorName);

		try {
			service.stopProcess(serverName, collectorName);
		} catch (ClientException e) {
			logger.warn(e.getMessage(), e);
		}

		append("Collector {0} has been stopped.", collectorName);
		append("Cleaning collector {0}...", collectorName);

		try {
			service.cleanupProcess(serverName, collectorName);
		} catch (ClientException e) {
			append("Can not cleanup collector, caused by: {0}.", e.getMessage());
			logger.warn("Can not cleanup collector", e);
		}

		append("Collector has been cleaned.");
		append("Updating collector {0} configuration...", collectorName);

		ConfigNode root = TreeParser.parse(content);
		Collection<ConfigNode> processes = findProcesses(root);

		if (processes.isEmpty()) {
			append("No processes found in config.", collectorName);
			logger.info("No processes found in config.");

			return null;
		}

		if (processes.size() > 1) {
			append("More than one processes found in config.", collectorName);
			logger.warn("More than one processes found in config.");

			return null;
		}

		ConfigNode source = processes.iterator().next();
		Config config = buildConfig(source);

		try {
			service.updateProcessConfig(serverName, collectorName, config);
		} catch (ClientException e) {
			append("Can not update collector configuration, caused by: {0}.",
					e.getMessage());
			logger.warn("Can not update collector configuration.", e);

			return null;
		}

		append("Configuration has been updated.");
		append("Starting collector {0}...", collectorName);

		try {
			service.startProcess(serverName, collectorName);
		} catch (ClientException e) {
			append("Can not start collector, caused by: {0}.", e.getMessage());
			logger.warn("Can start collector.", e);

			return null;
		}

		append("Collector started successfully.");

		return null;
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
