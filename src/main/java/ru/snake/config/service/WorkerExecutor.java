package ru.snake.config.service;

import java.io.File;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import ru.snake.config.model.ConfigModel;
import ru.snake.config.model.SettingsModel;
import ru.snake.config.task.LogTailHandler;
import ru.snake.config.worker.ByteTailerWorker;
import ru.snake.config.worker.CheckSyntaxWorker;
import ru.snake.config.worker.CollectorListWorker;
import ru.snake.config.worker.ConnectWorker;
import ru.snake.config.worker.DebugProcessWorker;
import ru.snake.config.worker.HighlightWorker;
import ru.snake.config.worker.LoadConfigWorker;
import ru.snake.config.worker.LoadFileWorker;
import ru.snake.config.worker.SaveFileWorker;
import ru.snake.config.worker.SaveSettingsWorker;
import ru.snake.config.worker.ServerListWorker;
import ru.snake.config.worker.StartProcessWorker;
import ru.snake.config.worker.StopProcessWorker;
import ru.snake.config.worker.SyntaxLoaderWorker;
import ru.snake.config.worker.TreeBuilderWorker;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class WorkerExecutor {

	private static final Logger logger = LoggerFactory
			.getLogger(WorkerExecutor.class);

	@Autowired
	private ConfigModel model;

	@Autowired
	private SiuService service;

	@Autowired
	private SettingsModel settings;

	@Autowired
	private SyntaxService syntax;

	private HighlightWorker highlightWorker;
	private TreeBuilderWorker treeBuilderWorker;
	private CheckSyntaxWorker checkSyntaxWorker;

	public void executeLoadConfig(String serverName, String processName) {
		LoadConfigWorker worker = new LoadConfigWorker();
		worker.setService(service);
		worker.setServerName(serverName);
		worker.setProcessName(processName);
		worker.setDocument(model.getConfigurationModel());
		worker.setStatusDocument(model.getStatusModel());
		worker.execute();
	}

	public void executeServerList() {
		ServerListWorker worker = new ServerListWorker();
		worker.setService(service);
		worker.setModel(model.getServersModel());
		worker.setStatusDocument(model.getStatusModel());
		worker.execute();
	};

	public void executeCollectorList(String serverName) {
		CollectorListWorker worker = new CollectorListWorker();
		worker.setService(service);
		worker.setModel(model.getCollectorsModel());
		worker.setServerName(serverName);
		worker.setStatusDocument(model.getStatusModel());
		worker.execute();
	}

	public void executeDebugProcess(String serverName, String processName) {
		DebugProcessWorker worker = new DebugProcessWorker();
		worker.setService(service);
		worker.setServerName(serverName);
		worker.setProcessName(processName);
		worker.setDocument(model.getConfigurationModel());
		worker.setStatusDocument(model.getStatusModel());
		worker.execute();
	}

	public void executeRunProcess(String serverName, String processName) {
		StartProcessWorker worker = new StartProcessWorker();
		worker.setService(service);
		worker.setServerName(serverName);
		worker.setProcessName(processName);
		worker.setDocument(model.getConfigurationModel());
		worker.setStatusDocument(model.getStatusModel());
		worker.execute();
	}

	public void executeStopProcess(String serverName, String processName) {
		StopProcessWorker worker = new StopProcessWorker();
		worker.setService(service);
		worker.setServerName(serverName);
		worker.setProcessName(processName);
		worker.setStatusDocument(model.getStatusModel());
		worker.execute();
	}

	public void executeLoadFile(File file) {
		LoadFileWorker worker = new LoadFileWorker();
		worker.setFile(file);
		worker.setDocument(model.getConfigurationModel());
		worker.execute();
	}

	private String getContent() {
		StyledDocument document = model.getConfigurationModel();

		try {
			return document.getText(0, document.getLength());
		} catch (BadLocationException e) {
			logger.error(e.getMessage(), e);

			return "";
		}
	}

	public void executeSaveFile(File file) {
		SaveFileWorker worker = new SaveFileWorker();
		worker.setFile(file);
		worker.setContent(getContent());
		worker.execute();
	}

	public void executeConnect(String ior) {
		ConnectWorker worker = new ConnectWorker();
		worker.setStatusDocument(model.getStatusModel());
		worker.setService(service);
		worker.setIorUrl(ior);
		worker.setAnonymous(true);
		worker.execute();
	}

	public void executeConnect(String ior, String username, String password) {
		ConnectWorker worker = new ConnectWorker();
		worker.setStatusDocument(model.getStatusModel());
		worker.setService(service);
		worker.setIorUrl(ior);
		worker.setAnonymous(false);
		worker.setUsername(username);
		worker.setPassword(password);
		worker.execute();
	}

	public void executeHighlight(StyledDocument document, int offset, int length) {
		if (highlightWorker != null) {
			if (!highlightWorker.isDone()) {
				highlightWorker.cancel(true);
			}
		}

		highlightWorker = new HighlightWorker();
		highlightWorker.setDocument(document);
		highlightWorker.setContent(getContent());
		highlightWorker.setStartPosition(offset);
		highlightWorker.setEndPosition(offset + length);
		highlightWorker.execute();
	}

	public void executeTreeBuilder() {
		if (treeBuilderWorker != null) {
			if (!treeBuilderWorker.isDone()) {
				treeBuilderWorker.cancel(true);
			}
		}

		treeBuilderWorker = new TreeBuilderWorker();
		treeBuilderWorker.setModel(model.getTreeModel());
		treeBuilderWorker.setContent(getContent());
		treeBuilderWorker.execute();
	}

	public void executeSaveSettings() {
		SaveSettingsWorker worker = new SaveSettingsWorker();
		worker.setStatusDocument(model.getStatusModel());
		worker.setSettings(settings);
		worker.execute();
	}

	public void executeByteTailer(String serverName, LogTailHandler handler) {
		ByteTailerWorker worker = new ByteTailerWorker();
		worker.setStatusDocument(model.getStatusModel());
		worker.setService(service);
		worker.setServerName(serverName);
		worker.setHandler(handler);
		worker.execute();
	}

	public void executeCheckSyntax() {
		if (checkSyntaxWorker != null) {
			if (!checkSyntaxWorker.isDone()) {
				checkSyntaxWorker.cancel(true);
			}
		}

		checkSyntaxWorker = new CheckSyntaxWorker();
		checkSyntaxWorker.setStatusDocument(model.getStatusModel());
		checkSyntaxWorker.setProblems(model.getProblemsModel());
		checkSyntaxWorker.setSyntax(syntax);
		checkSyntaxWorker.setContent(getContent());
		checkSyntaxWorker.execute();
	}

	public void executeSyntaxLoader(SyntaxHandler handler) {
		SyntaxLoaderWorker worker = new SyntaxLoaderWorker();
		worker.setStatusDocument(model.getStatusModel());
		worker.setHandler(handler);
		worker.execute();
	};

}
