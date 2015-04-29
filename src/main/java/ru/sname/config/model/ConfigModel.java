package ru.sname.config.model;

import java.io.File;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.TableModel;
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

import com.hp.siu.utils.ClientException;

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
	private TableModel problems;

	@Autowired
	private StringBoxModel servers;

	@Autowired
	private StringBoxModel collectors;

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

	public TableModel getProblemsModel() {
		return problems;
	}

	public StringBoxModel getServersModel() {
		return servers;
	}

	public StringBoxModel getCollectorsModel() {
		return collectors;
	}

	@Override
	public void onConnected() {
		try {
			Collection<String> list = siuService.getServers();

			servers.clear();
			servers.addAll(list);
		} catch (ClientException e) {
			return;
		}
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
			if (!siuService.isConnected()) {
				return;
			}

			String serverName = (String) servers.getSelectedItem();

			if (serverName == null) {
				return;
			}

			try {
				Collection<String> list = siuService.getCollectors(serverName);

				collectors.clear();
				collectors.addAll(list);
			} catch (ClientException e) {
				return;
			}
		}
	}

	@Override
	public void intervalAdded(ListDataEvent event) {
	}

	@Override
	public void intervalRemoved(ListDataEvent event) {
	}

}
