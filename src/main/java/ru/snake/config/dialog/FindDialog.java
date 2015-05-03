package ru.snake.config.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ru.snake.config.util.Attributes;

@Component
@SuppressWarnings("serial")
public class FindDialog extends JDialog implements ActionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(FindDialog.class);

	private JTextField findText;
	private JTextField replaceText;

	private JCheckBox caseSensitiveBox;
	private JCheckBox regularExpressionBox;
	private JCheckBox wrapSearchBox;

	private JButton findButton;
	private JButton replaceButton;
	private JButton closeButton;

	private JTextComponent textComponent;

	private void createComponents() {
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);

		JLabel findLabel = new JLabel("Find:");
		JLabel replaceLabel = new JLabel("Replace with:");

		findText = new JTextField("", 20);
		replaceText = new JTextField("", 20);
		caseSensitiveBox = new JCheckBox("Case sensitive", false);
		regularExpressionBox = new JCheckBox("Regular expression", false);
		wrapSearchBox = new JCheckBox("Wrap search", true);

		findLabel.setLabelFor(findText);
		replaceLabel.setLabelFor(replaceText);

		findButton = new JButton("Find");
		replaceButton = new JButton("Replace");
		closeButton = new JButton("Close");

		getRootPane().setDefaultButton(findButton);

		findButton.addActionListener(this);
		replaceButton.addActionListener(this);
		closeButton.addActionListener(this);

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
												.addComponent(findLabel)
												.addComponent(replaceLabel))
								.addGroup(
										layout.createParallelGroup(
												Alignment.LEADING, true)
												.addComponent(findText)
												.addComponent(replaceText)))
				.addComponent(caseSensitiveBox)
				.addComponent(regularExpressionBox)
				.addComponent(wrapSearchBox)
				.addGroup(
						Alignment.TRAILING,
						layout.createSequentialGroup().addComponent(findButton)
								.addComponent(replaceButton)
								.addComponent(closeButton)));

		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createBaselineGroup(true, false)
								.addComponent(findLabel).addComponent(findText))
				.addGroup(
						layout.createBaselineGroup(true, false)
								.addComponent(replaceLabel)
								.addComponent(replaceText))
				.addComponent(caseSensitiveBox)
				.addComponent(regularExpressionBox)
				.addComponent(wrapSearchBox)
				.addGroup(
						layout.createBaselineGroup(true, false)
								.addComponent(findButton)
								.addComponent(replaceButton)
								.addComponent(closeButton)));

		layout.linkSize(SwingConstants.HORIZONTAL, findText, replaceText);
		layout.linkSize(SwingConstants.HORIZONTAL, findButton, replaceButton,
				closeButton);

		add(panel);

		pack();
	}

	private void find() {
		String findWhat = findText.getText();
		boolean caseSensitive = caseSensitiveBox.isSelected();
		boolean regularExpression = regularExpressionBox.isSelected();
		boolean wrapSearch = wrapSearchBox.isSelected();
		int fromIndex = textComponent.getCaretPosition();

		if (!regularExpression) {
			findWhat = Pattern.quote(findWhat);
		}

		Pattern pattern;

		if (caseSensitive) {
			pattern = Pattern.compile(findWhat, Pattern.MULTILINE);
		} else {
			pattern = Pattern.compile(findWhat, Pattern.MULTILINE
					| Pattern.CASE_INSENSITIVE);
		}

		findPattern(pattern, fromIndex, wrapSearch);
	}

	private void findPattern(Pattern pattern, int fromIndex, boolean wrapSearch) {
		Document document = textComponent.getDocument();
		String content = getContent(document);
		Matcher matcher = pattern.matcher(content);

		if (matcher.find(fromIndex)) {
			textComponent.setSelectionStart(matcher.start());
			textComponent.setSelectionEnd(matcher.end());
			textComponent.requestFocusInWindow();
		} else if (wrapSearch && matcher.find(0)) {
			textComponent.setSelectionStart(matcher.start());
			textComponent.setSelectionEnd(matcher.end());
			textComponent.requestFocusInWindow();
		}
	}

	private String getContent(Document document) {
		String result;

		try {
			result = document.getText(0, document.getLength());
		} catch (BadLocationException e) {
			result = "";
		}

		return result;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();

		if (source == findButton) {
			find();
		} else if (source == replaceButton) {
			replace();
		} else if (source == closeButton) {
			setVisible(false);

			textComponent.requestFocusInWindow();
		}
	}

	private void replace() {
		String findWhat = findText.getText();
		String replacement = replaceText.getText();
		boolean caseSensitive = caseSensitiveBox.isSelected();
		boolean regularExpression = regularExpressionBox.isSelected();
		boolean wrapSearch = wrapSearchBox.isSelected();

		int fromIndex = textComponent.getCaretPosition();

		if (!regularExpression) {
			findWhat = Pattern.quote(findWhat);
		}

		Pattern pattern;

		if (caseSensitive) {
			pattern = Pattern.compile(findWhat, Pattern.MULTILINE);
		} else {
			pattern = Pattern.compile(findWhat, Pattern.MULTILINE
					| Pattern.CASE_INSENSITIVE);
		}

		replacePattern(pattern, replacement, fromIndex, wrapSearch);
	}

	private void replacePattern(Pattern pattern, String replacement,
			int fromIndex, boolean wrapSearch) {
		String selection = textComponent.getSelectedText();

		if (selection != null) {
			Matcher selectionMatcher = pattern.matcher(selection);

			if (selectionMatcher.matches()) {
				String subtitute = selectionMatcher.replaceFirst(replacement);
				int selectionStart = textComponent.getSelectionStart();
				int selectionEnd = textComponent.getSelectionEnd();

				substituteText(subtitute, selectionStart, selectionEnd);
			}
		}

		findPattern(pattern, fromIndex, wrapSearch);
	}

	private void substituteText(String replacement, int selectionStart,
			int selectionEnd) {
		Document document = textComponent.getDocument();

		try {
			document.remove(selectionStart, selectionEnd - selectionStart);
			document.insertString(selectionStart, replacement,
					Attributes.DEFAULT);
		} catch (BadLocationException e) {
			logger.warn("Unable to replace char sequence", e);
		}
	}

	@PostConstruct
	private void initialize() {
		logger.info("Creating FindDialog.");

		createComponents();

		setTitle("Find / Replace");

		setLocationRelativeTo(null);
		setResizable(false);
		setAutoRequestFocus(true);
		setAlwaysOnTop(true);

		logger.info("FindDialog created.");
	}

	@PreDestroy
	private void deinitialize() {
		logger.info("Destroying FindDialog.");

		dispose();

		logger.info("FindDialog destroyed.");
	}

	public void setSource(JTextComponent textComponent) {
		this.textComponent = textComponent;
	}

	public void setFindText(String text) {
		if (regularExpressionBox.isSelected()) {
			findText.setText(Pattern.quote(text));
		} else {
			findText.setText(text);
		}
	}

}
