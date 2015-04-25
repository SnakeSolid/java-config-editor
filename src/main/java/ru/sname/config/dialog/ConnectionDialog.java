package ru.sname.config.dialog;

import java.awt.Color;
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

import ru.sname.config.service.SiuService;

import com.hp.siu.utils.ClientException;

@Component
@SuppressWarnings("serial")
public class ConnectionDialog extends JDialog implements ActionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(ConnectionDialog.class);

	@Autowired
	private SiuService service;

	private JTextField iorText;
	private JCheckBox loginBox;
	private JTextField usernameText;
	private JTextField passwordText;
	private JLabel errorLabel;

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
		loginBox = new JCheckBox("Connect without security", true);
		usernameText = new JTextField(30);
		passwordText = new JTextField(30);

		errorLabel = new JLabel();
		errorLabel.setForeground(Color.RED);

		usernameText.setEditable(false);
		passwordText.setEditable(false);

		iorLabel.setLabelFor(iorText);
		usernameLabel.setLabelFor(usernameText);
		passworLabel.setLabelFor(passwordText);

		connectButton = new JButton("Connect");
		cancelButton = new JButton("Cancel");

		getRootPane().setDefaultButton(connectButton);

		loginBox.addActionListener(this);
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
												.addComponent(loginBox)
												.addComponent(usernameText)
												.addComponent(passwordText)))
				.addComponent(errorLabel, 0, GroupLayout.PREFERRED_SIZE, 450)
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
				.addComponent(loginBox)
				.addGroup(
						layout.createBaselineGroup(true, false)
								.addComponent(usernameLabel)
								.addComponent(usernameText))
				.addGroup(
						layout.createBaselineGroup(true, false)
								.addComponent(passworLabel)
								.addComponent(passwordText))
				.addComponent(errorLabel, GroupLayout.PREFERRED_SIZE,
						GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGroup(
						layout.createBaselineGroup(true, false)
								.addComponent(connectButton)
								.addComponent(cancelButton)));

		layout.linkSize(SwingConstants.HORIZONTAL, usernameText, passwordText);
		layout.linkSize(SwingConstants.HORIZONTAL, connectButton, cancelButton);

		add(panel);

		pack();
	}

	private boolean connect() {
		String ior = iorText.getText();
		String username = usernameText.getText();
		String password = passwordText.getText();

		try {
			if (username.length() == 0) {
				service.connect(ior);
			} else {
				service.connect(ior, username, password);
			}
		} catch (ClientException | IllegalArgumentException e) {
			errorLabel.setText(e.getMessage());

			pack();

			return false;
		}

		return true;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();

		if (source == loginBox) {
			boolean enable = loginBox.isSelected();

			if (enable) {
				usernameText.setEditable(false);
				passwordText.setEditable(false);
			} else {
				usernameText.setEditable(true);
				passwordText.setEditable(true);
			}
		} else if (source == connectButton) {
			if (connect()) {
				setVisible(false);
			}
		} else if (source == cancelButton) {
			errorLabel.setText("");

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
