package ru.sname.config.dialog;

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

import ru.sname.config.service.WorkerExecutor;

@Component
@SuppressWarnings("serial")
public class ConnectionDialog extends JDialog implements ActionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(ConnectionDialog.class);

	@Autowired
	private WorkerExecutor executor;

	private JTextField iorText;
	private JCheckBox anonymousBox;
	private JTextField usernameText;
	private JTextField passwordText;

	private JButton connectButton;
	private JButton cancelButton;

	private void createComponents() {
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);

		JLabel iorLabel = new JLabel("IOR URL:");
		JLabel usernameLabel = new JLabel("Username:");
		JLabel passworLabel = new JLabel("Password:");

		iorText = new JTextField("http://10.112.142.107:8158/", 30);
		anonymousBox = new JCheckBox("Anonymous login", true);
		usernameText = new JTextField(30);
		passwordText = new JTextField(30);

		usernameText.setEditable(false);
		passwordText.setEditable(false);

		iorLabel.setLabelFor(iorText);
		usernameLabel.setLabelFor(usernameText);
		passworLabel.setLabelFor(passwordText);

		connectButton = new JButton("Connect");
		cancelButton = new JButton("Cancel");

		getRootPane().setDefaultButton(connectButton);

		anonymousBox.addActionListener(this);
		connectButton.addActionListener(this);
		cancelButton.addActionListener(this);

		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);

		layout.setHorizontalGroup(layout
				.createParallelGroup()
				.addGroup(
						Alignment.CENTER,
						layout.createSequentialGroup()
								.addGroup(
										layout.createParallelGroup(
												Alignment.TRAILING, false)
												.addComponent(iorLabel)
												.addComponent(usernameLabel)
												.addComponent(passworLabel))
								.addGroup(
										layout.createParallelGroup(
												Alignment.LEADING, false)
												.addComponent(iorText)
												.addComponent(anonymousBox)
												.addComponent(usernameText)
												.addComponent(passwordText)))
				.addGroup(
						Alignment.TRAILING,
						layout.createSequentialGroup()
								.addComponent(connectButton)
								.addComponent(cancelButton)));

		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createBaselineGroup(true, false)
								.addComponent(iorLabel).addComponent(iorText))
				.addComponent(anonymousBox)
				.addGroup(
						layout.createBaselineGroup(true, false)
								.addComponent(usernameLabel)
								.addComponent(usernameText))
				.addGroup(
						layout.createBaselineGroup(true, false)
								.addComponent(passworLabel)
								.addComponent(passwordText))
				.addGroup(
						layout.createBaselineGroup(true, false)
								.addComponent(connectButton)
								.addComponent(cancelButton)));

		layout.linkSize(SwingConstants.HORIZONTAL, usernameText, passwordText);
		layout.linkSize(SwingConstants.HORIZONTAL, connectButton, cancelButton);

		add(panel);

		pack();
	}

	private void connect() {
		String ior = iorText.getText();
		boolean anonymous = anonymousBox.isSelected();
		String username = usernameText.getText();
		String password = passwordText.getText();

		if (anonymous) {
			executor.executeConnect(ior);
		} else {
			executor.executeConnect(ior, username, password);
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();

		if (source == anonymousBox) {
			boolean enable = anonymousBox.isSelected();

			if (enable) {
				usernameText.setEditable(false);
				passwordText.setEditable(false);
			} else {
				usernameText.setEditable(true);
				passwordText.setEditable(true);
			}
		} else if (source == connectButton) {
			connect();

			setVisible(false);
		} else if (source == cancelButton) {
			setVisible(false);
		}
	}

	@PostConstruct
	private void initialize() {
		logger.info("Creating ConnectionDialog.");

		createComponents();

		setTitle("Connect to server");

		setLocationRelativeTo(null);
		setResizable(false);
		setModal(true);

		logger.info("ConnectionDialog created.");
	}

	@PreDestroy
	private void deinitialize() {
		logger.info("Destroying ConnectionDialog.");

		dispose();

		logger.info("ConnectionDialog destroyed.");
	}

}
