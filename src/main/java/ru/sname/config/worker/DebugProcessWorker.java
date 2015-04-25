package ru.sname.config.worker;

import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.sname.config.service.SiuService;

import com.hp.siu.utils.Config;
import com.hp.siu.utils.InvalidStateException;

public class DebugProcessWorker extends SwingWorker<Void, Void> {

	private static final Logger logger = LoggerFactory
			.getLogger(HighlightWorker.class);

	private SiuService service;
	private String serverName;
	private String collectorName;
	private String content;

	@Override
	protected Void doInBackground() throws Exception {
		try {
			service.stopProcess(serverName, collectorName);
		} catch (InvalidStateException e) {
			logger.warn(e.getMessage(), e);
		}

		service.cleanupProcess(serverName, collectorName);

		Config node = new Config(collectorName);

		service.updateProcessConfig(serverName, collectorName, node);
		service.startProcess(serverName, collectorName);

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
		this.content = getContent(document);
	}

}
