package ru.sname.config.model;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import ru.sname.config.service.SiuListener;
import ru.sname.config.service.SiuService;
import ru.sname.config.service.WorkerExecutor;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ConfigModel implements SiuListener, ListDataListener {

	private File workingDirectory;
	private File workingFile;
	private StyledDocument configuration;
	private DefaultTreeModel treeModel;
	private StyledDocument status;
	private StyledDocument log;

	@Autowired
	private SiuService siuService;

	@Autowired
	private ProblemsTableModel problems;

	@Autowired
	private DetailsTableModel details;

	@Autowired
	private StringBoxModel servers;

	@Autowired
	private StringBoxModel collectors;

	@Autowired
	private WorkerExecutor executor;

	@PostConstruct
	private void initialize() {
		workingDirectory = new File(System.getProperty("user.dir"));
		workingFile = null;
		configuration = new DefaultStyledDocument();
		status = new DefaultStyledDocument();
		log = new DefaultStyledDocument();

		treeModel = new DefaultTreeModel(null);
		treeModel.setRoot(new DefaultMutableTreeNode());

		siuService.addListener(this);
		servers.addListDataListener(this);
	}

	public File getWorkingDirectory() {
		return workingDirectory;
	}

	public void setWorkingDirectory(File workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	public StyledDocument getConfigurationModel() {
		return configuration;
	}

	public StyledDocument getStatusModel() {
		return status;
	}

	public StyledDocument getLogModel() {
		return log;
	}

	public ProblemsTableModel getProblemsModel() {
		return problems;
	}

	public DetailsTableModel getDetailsModel() {
		return details;
	}

	public StringBoxModel getServersModel() {
		return servers;
	}

	public StringBoxModel getCollectorsModel() {
		return collectors;
	}

	@Override
	public void onConnected() {
		servers.clear();
		executor.executeServerList();
	}

	@Override
	public void onDisconnected() {
		servers.clear();
	}

	public void setWorkingFile(File workingFile) {
		this.workingFile = workingFile;
	}

	public File getWorkingFile() {
		return workingFile;
	}

	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}

	@Override
	public void contentsChanged(ListDataEvent event) {
		Object source = event.getSource();

		if (source == servers) {
			collectors.clear();

			if (!siuService.isConnected()) {
				return;
			}

			String serverName = (String) servers.getSelectedItem();
			executor.executeCollectorList(serverName);
		}
	}

	@Override
	public void intervalAdded(ListDataEvent event) {
	}

	@Override
	public void intervalRemoved(ListDataEvent event) {
	}

}
