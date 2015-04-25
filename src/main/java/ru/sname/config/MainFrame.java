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
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ru.sname.config.listener.DocumentHighlightListener;
import ru.sname.config.listener.UpdateTreeListener;
import ru.sname.config.model.ConfigModel;

@Component
public class MainFrame extends JFrame {

	private static final int MINIMAL_BOX_SIZE = 300;

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

	private void createComponents() {
		createMenu();

		JToolBar toolbar = createToolbar();

		JTextPane configText = new JTextPane(model.getConfigurationModel());
		JScrollPane configPane = new JScrollPane(configText,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		configPane.setPreferredSize(new Dimension(720, 480));

		// -------------------------

		Font monospacedFont = new Font(Font.MONOSPACED, Font.PLAIN, 14);
		configText.setFont(monospacedFont);

		// -------------------------

		InputMap inputMap = configText.getInputMap();
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
		infoPane.addTab("Log output", new JScrollPane(new JTextArea(),
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

		// -------------------------

		JSplitPane configSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		configSplitPane.setTopComponent(configPane);
		configSplitPane.setBottomComponent(infoPane);
		configSplitPane.setDividerLocation(700);

		// -------------------------

		JTree treePane = new JTree(model.getTreeModel());
		treePane.setRootVisible(false);

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

		JComboBox<String> serverBox = new JComboBox<String>(
				model.getServersModel());
		JComboBox<String> collectorBox = new JComboBox<String>(
				model.getCollectorsModel());
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
								.addComponent(serverBox, MINIMAL_BOX_SIZE,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(collectorBox, MINIMAL_BOX_SIZE,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE)));

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

		addWindowListener(exitOnClose);

		setTitle("Configuration editor");
		setVisible(true);

		logger.info("MainFrame created.");
	}

	@PreDestroy
	private void deinitialize() {
		logger.info("Destroying MainFrame.");

		removeWindowListener(exitOnClose);

		dispose();

		logger.info("MainFrame destroyed.");
	}

}
