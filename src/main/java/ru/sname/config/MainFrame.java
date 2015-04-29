package ru.sname.config;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowListener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.undo.UndoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ru.sname.config.action.EditRedoAction;
import ru.sname.config.action.EditUndoAction;
import ru.sname.config.action.TreeGotoAction;
import ru.sname.config.action.TreeSelectAction;
import ru.sname.config.listener.DocumentHighlightListener;
import ru.sname.config.listener.FilterUndoListener;
import ru.sname.config.listener.TreePopupMenuListener;
import ru.sname.config.listener.UpdateTreeListener;
import ru.sname.config.model.ConfigModel;
import ru.sname.config.service.SiuListener;
import ru.sname.config.service.SiuService;

@Component
public class MainFrame extends JFrame implements SiuListener {

	private static final long serialVersionUID = -8561388914130543345L;

	private static final Logger logger = LoggerFactory
			.getLogger(MainFrame.class);

	@Autowired
	private WindowListener exitOnClose;

	@Autowired
	@Qualifier("application_new_action")
	private Action applicationNewAction;

	@Autowired
	@Qualifier("application_open_action")
	private Action applicationOpenAction;

	@Autowired
	@Qualifier("application_save_action")
	private Action applicationSaveAction;

	@Autowired
	@Qualifier("application_saveas_action")
	private Action applicationSaveAsAction;

	@Autowired
	@Qualifier("application_exit_action")
	private Action applicationExitAction;

	@Autowired
	@Qualifier("configuration_debug_action")
	private Action configurationDebugAction;

	@Autowired
	@Qualifier("configuration_run_action")
	private Action configurationRunAction;

	@Autowired
	@Qualifier("configuration_stop_action")
	private Action configurationStopAction;

	@Autowired
	@Qualifier("configuration_load_action")
	private Action configurationLoadAction;

	@Autowired
	@Qualifier("server_connect_action")
	private Action serverConnectAction;

	@Autowired
	@Qualifier("server_disconnect_action")
	private Action serverDisconnectAction;

	@Autowired
	private ConfigModel model;

	@Autowired
	private SiuService service;

	@Autowired
	private LogTailer tailer;

	JComboBox<String> serverBox;
	JComboBox<String> collectorBox;

	private void createComponents() {
		createMenu();

		// -------------------------

		Font monospacedFont = new Font(Font.MONOSPACED, Font.PLAIN, 14);
		Font sansSerifFont = new Font(Font.SANS_SERIF, Font.PLAIN, 14);

		// -------------------------

		JToolBar toolbar = createToolbar();

		JTextPane configText = new JTextPane(model.getConfigurationModel());
		JScrollPane configPane = new JScrollPane(configText,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		configPane.setPreferredSize(new Dimension(720, 480));

		// -------------------------

		InputMap inputMap = configText.getInputMap();
		UndoManager undoManager = new UndoManager();
		FilterUndoListener editFiler = new FilterUndoListener(undoManager);
		model.getConfigurationModel().addUndoableEditListener(editFiler);

		inputMap = configText.getInputMap();
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
				InputEvent.CTRL_DOWN_MASK), new EditUndoAction(undoManager));
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
				InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK),
				new EditRedoAction(undoManager));

		// -------------------------

		configText.setFont(monospacedFont);

		// -------------------------

		inputMap = configText.getInputMap();
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,
				InputEvent.CTRL_DOWN_MASK), "suggest");

		ActionMap actionMap = configText.getActionMap();
		actionMap.put("suggest", new AbstractAction() {
			private static final long serialVersionUID = -1688439548174896960L;

			@Override
			public void actionPerformed(ActionEvent e) {
				JTextPane source = (JTextPane) e.getSource();
				int position = source.getCaretPosition();
				Rectangle view;

				try {
					view = source.modelToView(position);
				} catch (BadLocationException exception) {
					logger.error("Cannot get screen position of caret",
							exception);

					return;
				}

				Point offset = source.getLocationOnScreen();
				JList<String> suggestions = new JList<String>(new String[] {
						"Test", "Test", "Test" });
				PopupFactory factory = PopupFactory.getSharedInstance();
				final Popup popup = factory.getPopup(source, suggestions,
						offset.x + view.x, offset.y + view.y + view.height);
				popup.show();

				suggestions.requestFocus();
				suggestions.addFocusListener(new FocusListener() {
					@Override
					public void focusLost(FocusEvent e) {
						popup.hide();
					}

					@Override
					public void focusGained(FocusEvent e) {
					}
				});
			}
		});

		// -------------------------

		JTabbedPane infoPane = new JTabbedPane();
		infoPane.addTab("Problems",
				new JScrollPane(new JTable(model.getProblemsModel()),
						JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
						JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

		JTextArea logText = new JTextArea(model.getLogModel());
		logText.setFont(sansSerifFont);
		logText.setEditable(false);

		JScrollPane logScroll = new JScrollPane(logText,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		infoPane.addTab("Log output", logScroll);

		JTextArea statusText = new JTextArea(model.getStatusModel());
		statusText.setFont(sansSerifFont);
		statusText.setEditable(false);

		JScrollPane statusScroll = new JScrollPane(statusText,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		infoPane.addTab("Status", statusScroll);

		infoPane.setSelectedComponent(statusScroll);

		// -------------------------

		JSplitPane configSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		configSplitPane.setTopComponent(configPane);
		configSplitPane.setBottomComponent(infoPane);
		configSplitPane.setDividerLocation(700);

		// -------------------------

		JTree treePane = new JTree(model.getTreeModel());
		treePane.setRootVisible(false);

		createPopupMenu(treePane, configText);

		JScrollPane treeScroll = new JScrollPane(treePane);
		treeScroll
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		treeScroll
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// -------------------------

		StyledDocument doc = model.getConfigurationModel();
		doc.addDocumentListener(new DocumentHighlightListener());
		doc.addDocumentListener(new UpdateTreeListener(model.getTreeModel()));

		// -------------------------

		JSplitPane treeSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		treeSplitPane.setLeftComponent(treeScroll);
		treeSplitPane.setRightComponent(configSplitPane);
		treeSplitPane.setDividerLocation(200);

		// -------------------------

		add(toolbar, BorderLayout.NORTH);
		add(treeSplitPane, BorderLayout.CENTER);

		pack();
	}

	private void createPopupMenu(final JTree treePane, JTextPane configText) {
		JPopupMenu popup = new JPopupMenu();
		popup.add(new TreeGotoAction(treePane, configText));
		popup.add(new TreeSelectAction(treePane, configText));

		treePane.addMouseListener(new TreePopupMenuListener(treePane, popup));
	}

	private JToolBar createToolbar() {
		JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
		toolbar.setFloatable(false);

		JButton connectButton = new JButton(serverConnectAction);
		JButton debugButton = new JButton(configurationDebugAction);
		JButton runButton = new JButton(configurationRunAction);
		JButton stopButton = new JButton(configurationStopAction);
		JButton loadButton = new JButton(configurationLoadAction);

		JLabel serverLabel = new JLabel("Server:");
		JLabel collectorLabel = new JLabel("Collector:");

		serverBox = new JComboBox<String>(model.getServersModel());
		collectorBox = new JComboBox<String>(model.getCollectorsModel());
		collectorBox.setEditable(true);

		JPanel destinationPane = new JPanel();
		destinationPane.setOpaque(false);

		GroupLayout layout = new GroupLayout(destinationPane);
		destinationPane.setLayout(layout);

		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		layout.setHorizontalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(serverLabel)
								.addComponent(collectorLabel))
				.addGroup(
						layout.createParallelGroup(Alignment.TRAILING, true)
								.addComponent(serverBox)
								.addComponent(collectorBox)));

		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createBaselineGroup(false, false)
								.addComponent(serverLabel)
								.addComponent(serverBox))
				.addGroup(
						layout.createBaselineGroup(false, false)
								.addComponent(collectorLabel)
								.addComponent(collectorBox)));

		layout.linkSize(SwingConstants.HORIZONTAL, serverBox, collectorBox);

		toolbar.add(connectButton);
		toolbar.addSeparator();
		toolbar.add(debugButton);
		toolbar.add(runButton);
		toolbar.add(stopButton);
		toolbar.addSeparator();
		toolbar.add(loadButton);
		toolbar.addSeparator();
		toolbar.add(destinationPane);

		return toolbar;
	}

	private void createMenu() {
		JMenu applicationMenu = new JMenu("Application");
		applicationMenu.setMnemonic('a');
		applicationMenu.add(applicationNewAction);
		applicationMenu.add(applicationOpenAction);
		applicationMenu.add(applicationSaveAction);
		applicationMenu.add(applicationSaveAsAction);
		applicationMenu.addSeparator();
		applicationMenu.add(applicationExitAction);

		JMenu serverMenu = new JMenu("Server");
		serverMenu.setMnemonic('s');
		serverMenu.add(serverConnectAction);
		serverMenu.add(serverDisconnectAction);

		JMenu configurationMenu = new JMenu("Configuration");
		configurationMenu.setMnemonic('c');
		configurationMenu.add(configurationDebugAction);
		configurationMenu.add(configurationRunAction);
		configurationMenu.add(configurationStopAction);
		configurationMenu.addSeparator();
		configurationMenu.add(configurationLoadAction);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('h');

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(applicationMenu);
		menuBar.add(serverMenu);
		menuBar.add(configurationMenu);
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);
	}

	@PostConstruct
	private void initialize() {
		logger.info("Creating MainFrame.");

		createComponents();
		setRemoteActionsEnaled(false);

		addWindowListener(exitOnClose);
		service.addListener(this);

		setTitle("Configuration editor");
		setVisible(true);

		logger.info("MainFrame created.");
	}

	@PreDestroy
	private void deinitialize() {
		logger.info("Destroying MainFrame.");

		removeWindowListener(exitOnClose);
		service.removeListener(this);

		dispose();

		logger.info("MainFrame destroyed.");
	}

	@Override
	public void onConnected() {
		setRemoteActionsEnaled(true);
	}

	private void setRemoteActionsEnaled(boolean value) {
		configurationDebugAction.setEnabled(value);
		configurationRunAction.setEnabled(value);
		configurationStopAction.setEnabled(value);
		configurationLoadAction.setEnabled(value);
		serverDisconnectAction.setEnabled(value);

		serverBox.setEnabled(value);
		collectorBox.setEnabled(value);
	}

	@Override
	public void onDisconnected() {
		setRemoteActionsEnaled(false);

		serverBox.setSelectedItem(null);
		collectorBox.setSelectedItem(null);
	}

}
