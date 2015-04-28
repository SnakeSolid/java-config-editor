package ru.sname.config.worker;

import java.util.Enumeration;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.sname.config.service.SiuService;
import ru.sname.config.worker.util.Attributes;

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
		append("Loading configuration for {0}.", collectorName);

		content = new StringBuilder(DEFAULT_CAPACITY);

		Config tree;

		try {
			tree = service.getConfigTree(serverName, collectorName);
		} catch (Exception e) {
			append("Can not load configuration for {0}, caused by: {1}.",
					collectorName, e.getMessage());
			logger.error(e.getMessage(), e);

			return null;
		}

		walkTree(tree);

		append("Configuration has been loaded.");

		return null;
	}

	private void walkTree(Config node) {
		content.append('[');
		content.append(node.getFullName());
		content.append("]\n\n");

		@SuppressWarnings("unchecked")
		Enumeration<String> attributes = node.getAttributeNames();

		while (attributes.hasMoreElements()) {
			String attribute = attributes.nextElement();

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

		while (children.hasMoreElements()) {
			String childName = children.nextElement();
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
