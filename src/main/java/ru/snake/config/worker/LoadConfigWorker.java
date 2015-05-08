package ru.snake.config.worker;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.snake.config.service.SiuService;
import ru.snake.config.util.Attributes;

import com.hp.siu.utils.Config;
import com.hp.siu.utils.ShallowEntryException;

public class LoadConfigWorker extends AbstractSuiWorker {

	private static final Logger logger = LoggerFactory
			.getLogger(HighlightWorker.class);

	private static final int DEFAULT_CAPACITY = 65536;

	private StyledDocument document;
	private StringBuilder content;

	@Override
	protected Void doInBackground() throws Exception {
		if (serverName == null) {
			warn("Server does not chosen.");

			return null;
		}

		if (processName == null) {
			warn("Process does not chosen.");

			return null;
		}

		info("Loading configuration for {0}.", processName);

		content = new StringBuilder(DEFAULT_CAPACITY);

		Config tree;

		try {
			tree = service.getConfigTree(serverName, processName);
		} catch (Exception e) {
			warn("Can not load configuration for {0}, caused by: {1}.",
					processName, e.getMessage());
			logger.error(e.getMessage(), e);

			return null;
		}

		walkTree(tree);

		info("Configuration has been loaded.");

		return null;
	}

	private void walkTree(Config node) {
		content.append('[');
		content.append(node.getFullName());
		content.append("]\n\n");

		@SuppressWarnings("unchecked")
		Enumeration<String> attributes = node.getAttributeNames();
		List<String> attrinbuteNames = new LinkedList<String>();

		while (attributes.hasMoreElements()) {
			attrinbuteNames.add(attributes.nextElement());
		}

		Collections.sort(attrinbuteNames);

		for (String attribute : attrinbuteNames) {
			for (String value : node.getAttributes(attribute)) {
				content.append(attribute);
				content.append('=');
				content.append(value);
				content.append('\n');
			}
		}

		content.append("\n\n");

		@SuppressWarnings("unchecked")
		Enumeration<String> children = node.getConfigNames();
		List<String> childNames = new LinkedList<String>();

		while (children.hasMoreElements()) {
			childNames.add(children.nextElement());
		}

		Collections.sort(childNames);

		for (String childName : childNames) {
			Config child;

			try {
				child = node.getConfigEntry(childName);
			} catch (ShallowEntryException e) {
				logger.error(e.getMessage(), e);

				continue;
			}

			walkTree(child);
		}
	}

	@Override
	protected void done() {
		if (content == null) {
			return;
		}

		try {
			document.remove(0, document.getLength());
			document.insertString(0, content.toString(), Attributes.DEFAULT);
		} catch (BadLocationException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void setService(SiuService service) {
		this.service = service;
	}

	public void setDocument(StyledDocument document) {
		this.document = document;
	}

}
