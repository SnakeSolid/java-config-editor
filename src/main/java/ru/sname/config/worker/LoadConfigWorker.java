package ru.sname.config.worker;

import java.util.Enumeration;

import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.sname.config.service.SiuService;

import com.hp.siu.utils.Config;
import com.hp.siu.utils.ShallowEntryException;

public class LoadConfigWorker extends SwingWorker<Void, Void> {

	private static final Logger logger = LoggerFactory
			.getLogger(HighlightWorker.class);

	private static final int DEFAULT_CAPACITY = 65536;

	private static final SimpleAttributeSet DEFAULT_STYLE;

	static {
		DEFAULT_STYLE = new SimpleAttributeSet();
	}

	private SiuService service;
	private String serverName;
	private String collectorName;
	private StyledDocument document;

	private StringBuilder content;

	@Override
	protected Void doInBackground() throws Exception {
		content = new StringBuilder(DEFAULT_CAPACITY);

		Config tree;

		try {
			tree = service.getConfigTree(serverName, collectorName);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);

			return null;
		}

		walkTree(tree);

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
			document.insertString(0, content.toString(), DEFAULT_STYLE);
		} catch (BadLocationException e) {
			logger.error(e.getMessage(), e);
		}
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

	public void setDocument(StyledDocument document) {
		this.document = document;
	}

}
