package ru.snake.config.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.snake.config.model.SettingsModel;
import ru.snake.config.service.WorkerExecutor;

@Component
@SuppressWarnings("serial")
public class SettingsDialog extends JDialog implements ActionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(SettingsDialog.class);

	@Autowired
	private WorkerExecutor executor;

	@Autowired
	private SettingsModel settings;

	private JTextField portText;
	private JCheckBox suspendBox;

	private JButton saveButton;
	private JButton cancelButton;

	private void createComponents() {
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);

		JLabel portLabel = new JLabel("Debug port:");

		portText = new JTextField(String.valueOf(settings.getDebugPort()), 10);
		suspendBox = new JCheckBox("Suspended", settings.getDebugSuspend());

		portLabel.setLabelFor(portText);

		saveButton = new JButton("Save");
		cancelButton = new JButton("Cancel");

		getRootPane().setDefaultButton(saveButton);

		saveButton.addActionListener(this);
		cancelButton.addActionListener(this);

		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);

		layout.setHorizontalGroup(layout
				.createParallelGroup()
				.addGroup(
						Alignment.CENTER,
						layout.createSequentialGroup()
								.addComponent(portLabel)
								.addGroup(
										layout.createParallelGroup(
												Alignment.LEADING, false)
												.addComponent(portText)
												.addComponent(suspendBox)))
				.addGroup(
						Alignment.TRAILING,
						layout.createSequentialGroup().addComponent(saveButton)
								.addComponent(cancelButton)));

		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createBaselineGroup(true, false)
								.addComponent(portLabel).addComponent(portText))
				.addComponent(suspendBox)
				.addGroup(
						layout.createBaselineGroup(true, false)
								.addComponent(saveButton)
								.addComponent(cancelButton)));

		layout.linkSize(SwingConstants.HORIZONTAL, saveButton, cancelButton);

		add(panel);

		pack();
	}

	private void save() {
		String portTest = portText.getText();
		boolean suspend = suspendBox.isSelected();
		int port = 8000;

		try {
			port = Integer.parseInt(portTest);
		} catch (NumberFormatException e) {
			logger.warn("Failed to parse debug port", e);
		}

		settings.setDebugPort(port);
		settings.setDebugSuspend(suspend);
		executor.executeSaveSettings();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();

		if (source == saveButton) {
			save();

			setVisible(false);
		} else if (source == cancelButton) {
			setVisible(false);
		}
	}

	@PostConstruct
	private void initialize() {
		logger.info("Creating SettingsDialog.");

		createComponents();

		setTitle("Connect to server");

		setLocationRelativeTo(null);
		setResizable(false);
		setModal(true);

		logger.info("SettingsDialog created.");
	}

	@PreDestroy
	private void deinitialize() {
		logger.info("Destroying SettingsDialog.");

		dispose();

		logger.info("SettingsDialog destroyed.");
	}

}
