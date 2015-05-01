package ru.sname.config.worker;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

import javax.swing.SwingWorker;
import javax.swing.text.StyledDocument;

import ru.sname.config.util.Attributes;
import ru.sname.config.util.Patterns;
import ru.sname.config.util.Token;
import ru.sname.config.util.TokenType;

public class HighlightWorker extends SwingWorker<Void, Void> {

	private StyledDocument document;
	private String content;
	private List<Token> tokens;
	private int startPosition;
	private int endPosition;

	public HighlightWorker() {
		this.tokens = new LinkedList<Token>();
	}

	public void setDocument(StyledDocument document) {
		this.document = document;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	public void setEndPosition(int endPosition) {
		this.endPosition = endPosition;
	}

	private void findPosition(int regionStart, int regionEnd) {
		if (regionStart >= content.length()) {
			startPosition = content.length() - 1;
			endPosition = content.length() - 1;

			return;
		}

		Matcher matcher = Patterns.CATEGORY.matcher(content);
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

	@Override
	protected Void doInBackground() throws Exception {
		if (content == null) {
			return null;
		}

		if (content.length() == 0) {
			return null;
		}

		findPosition(startPosition, endPosition);

		Matcher matcher = Patterns.LINE.matcher(content);
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
				} else if (line.startsWith("[/")) {
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
						token.getLength(), Attributes.CATHEGORY, true);
				break;

			case ATTRIBUTE:
				document.setCharacterAttributes(token.getStart(),
						token.getLength(), Attributes.ATTRIBUTE, true);
				break;

			case EQUALS_SIGN:
				document.setCharacterAttributes(token.getStart(),
						token.getLength(), Attributes.EQUALS, true);
				break;

			case VALUE:
				document.setCharacterAttributes(token.getStart(),
						token.getLength(), Attributes.VALUE, true);
				break;

			case COMMENT:
				document.setCharacterAttributes(token.getStart(),
						token.getLength(), Attributes.COMMENT, true);
				break;

			case ERROR:
				document.setCharacterAttributes(token.getStart(),
						token.getLength(), Attributes.ERROR, true);
				break;
			}
		}
	}

}
