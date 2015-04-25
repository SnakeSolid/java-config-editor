package ru.sname.config.worker;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.sname.config.worker.util.Token;
import ru.sname.config.worker.util.TokenType;

public class HighlightWorker extends SwingWorker<Void, Void> {

	private static final Logger logger = LoggerFactory
			.getLogger(HighlightWorker.class);

	private static final Pattern LINE_PATTERN;
	private static final Pattern CATEGORY_PATTERN;

	private static final SimpleAttributeSet CONFIG_CATHEGORY;
	private static final SimpleAttributeSet CONFIG_ATTRIBUTE;
	private static final SimpleAttributeSet CONFIG_EQUALS;
	private static final SimpleAttributeSet CONFIG_VALUE;
	private static final SimpleAttributeSet CONFIG_COMMENT;
	private static final SimpleAttributeSet CONFIG_ERROR;

	static {
		LINE_PATTERN = Pattern.compile("[\\r\\n]+", Pattern.MULTILINE);
		CATEGORY_PATTERN = Pattern.compile("^\\[", Pattern.MULTILINE);

		CONFIG_CATHEGORY = new SimpleAttributeSet();
		StyleConstants.setForeground(CONFIG_CATHEGORY, Color.BLUE);
		StyleConstants.setBold(CONFIG_CATHEGORY, true);

		CONFIG_ATTRIBUTE = new SimpleAttributeSet();
		StyleConstants.setForeground(CONFIG_ATTRIBUTE, Color.BLACK);
		StyleConstants.setBold(CONFIG_ATTRIBUTE, true);

		CONFIG_EQUALS = new SimpleAttributeSet();
		StyleConstants.setForeground(CONFIG_EQUALS, Color.GRAY);

		CONFIG_VALUE = new SimpleAttributeSet();
		StyleConstants.setForeground(CONFIG_VALUE, Color.BLUE);

		CONFIG_COMMENT = new SimpleAttributeSet();
		StyleConstants.setForeground(CONFIG_COMMENT, Color.GRAY);

		CONFIG_ERROR = new SimpleAttributeSet();
		StyleConstants.setForeground(CONFIG_ERROR, Color.RED);
		StyleConstants.setBold(CONFIG_ERROR, true);
	}

	private final StyledDocument document;
	private final String content;

	private List<Token> tokens;
	private int startPosition;
	private int endPosition;

	public HighlightWorker(StyledDocument document, int offset, int length) {
		this.document = document;
		this.content = getDocumentContent(document);

		this.tokens = new LinkedList<Token>();
		this.startPosition = offset;
		this.endPosition = offset + length;
	}

	private void findPosition(int regionStart, int regionEnd) {
		if (regionStart >= content.length()) {
			startPosition = content.length() - 1;
			endPosition = content.length() - 1;

			return;
		}

		Matcher matcher = CATEGORY_PATTERN.matcher(content);
		int index;

		startPosition = 0;
		index = startPosition;

		while (matcher.find(index)) {
			int start = matcher.start();
			int end = matcher.end();

			if (index < start) {
				if (start < regionStart) {
					startPosition = start;
				} else {
					break;
				}
			}

			index = end;
		}

		endPosition = regionEnd;
		index = regionEnd;

		while (matcher.find(index)) {
			int start = matcher.start();
			int end = matcher.end();

			if (index < start) {
				if (start > regionEnd) {
					endPosition = start;

					break;
				}
			}

			index = end;
		}
	}

	private String getDocumentContent(StyledDocument document) {
		try {
			return document.getText(0, document.getLength());
		} catch (BadLocationException e) {
			logger.error(e.getMessage(), e);

			return null;
		}
	}

	@Override
	protected Void doInBackground() throws Exception {
		if (content == null) {
			return null;
		}

		if (content.length() == 0) {
			return null;
		}

		findPosition(startPosition, endPosition);

		Matcher matcher = LINE_PATTERN.matcher(content);
		int index = startPosition;

		while (matcher.find(index)) {
			int start = matcher.start();
			int end = matcher.end();

			if (endPosition < start) {
				break;
			}

			if (index < start) {
				String line = content.substring(index, start);

				if (line.startsWith("#")) {
					scanComment(line, index, start);
				} else if (line.startsWith("[")) {
					scanCathegory(line, index, start);
				} else {
					scanAttribute(line, index, start);
				}
			}

			index = end;
		}

		return null;
	}

	private void scanComment(String line, int start, int end) {
		addToken(start, end - start, line, TokenType.COMMENT);
	}

	private void scanAttribute(String line, int start, int end) {
		int equalsIndex = line.indexOf('=');

		if (equalsIndex == -1) {
			addToken(start, end - start, line, TokenType.ERROR);
		} else if (equalsIndex == 0) {
			addToken(start, end - start, line, TokenType.ERROR);
		} else if (equalsIndex == line.length() - 1) {
			String attribute = line.substring(0, equalsIndex);
			String equalsSign = line.substring(equalsIndex);

			addToken(start, equalsIndex, attribute, TokenType.ATTRIBUTE);
			addToken(start + equalsIndex, 1, equalsSign, TokenType.EQUALS_SIGN);
		} else {
			String attribute = line.substring(0, equalsIndex);
			String equalsSign = line.substring(equalsIndex, equalsIndex + 1);
			String theValue = line.substring(equalsIndex + 1);

			addToken(start, equalsIndex, attribute, TokenType.ATTRIBUTE);
			addToken(start + equalsIndex, 1, equalsSign, TokenType.EQUALS_SIGN);
			addToken(start + equalsIndex + 1, line.length() - equalsIndex - 1,
					theValue, TokenType.VALUE);
		}
	}

	private void addToken(int start, int length, String value, TokenType type) {
		Token token = new Token();
		token.setStart(start);
		token.setLength(length);
		token.setText(value);
		token.setType(type);

		tokens.add(token);
	}

	private void scanCathegory(String line, int start, int end) {
		if (line.startsWith("[/") && line.endsWith("]")) {
			addToken(start, end - start, line, TokenType.CATEGORY);
		} else {
			addToken(start, end - start, line, TokenType.ERROR);
		}
	}

	@Override
	protected void done() {
		for (Token token : tokens) {
			if (isCancelled()) {
				break;
			}

			switch (token.getType()) {
			case CATEGORY:
				document.setCharacterAttributes(token.getStart(),
						token.getLength(), CONFIG_CATHEGORY, true);
				break;

			case ATTRIBUTE:
				document.setCharacterAttributes(token.getStart(),
						token.getLength(), CONFIG_ATTRIBUTE, true);
				break;

			case EQUALS_SIGN:
				document.setCharacterAttributes(token.getStart(),
						token.getLength(), CONFIG_EQUALS, true);
				break;

			case VALUE:
				document.setCharacterAttributes(token.getStart(),
						token.getLength(), CONFIG_VALUE, true);
				break;

			case COMMENT:
				document.setCharacterAttributes(token.getStart(),
						token.getLength(), CONFIG_COMMENT, true);
				break;

			case ERROR:
				document.setCharacterAttributes(token.getStart(),
						token.getLength(), CONFIG_ERROR, true);
				break;
			}
		}
	}

}
