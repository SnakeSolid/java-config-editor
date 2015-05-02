package ru.snake.config.worker;

import java.util.Collection;
import java.util.LinkedList;

import ru.snake.config.model.ProblemsTableModel;
import ru.snake.config.service.SyntaxService;
import ru.snake.config.syntax.error.SyntaxError;
import ru.snake.config.tree.ConfigNode;
import ru.snake.config.tree.TreeParser;

public class CheckSyntaxWorker extends AbstractConfigWorker {

	private SyntaxService syntax;
	private String content;
	private ProblemsTableModel problems;

	private Collection<SyntaxError> errors;

	public CheckSyntaxWorker() {
		errors = new LinkedList<SyntaxError>();
	}

	public void setSyntax(SyntaxService syntax) {
		this.syntax = syntax;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setProblems(ProblemsTableModel problems) {
		this.problems = problems;
	}

	@Override
	protected Void doInBackground() throws Exception {
		if (content == null) {
			return null;
		}

		if (content.length() == 0) {
			return null;
		}

		ConfigNode root = TreeParser.parse(content);

		processNode(root);

		return null;
	}

	private void processNode(ConfigNode node) {
		if (isCancelled()) {
			return;
		}

		errors.addAll(syntax.checkNode(node));

		for (ConfigNode child : node.getChildren()) {
			processNode(child);
		}
	}

	@Override
	protected void done() {
		if (isCancelled()) {
			return;
		}

		problems.setDetails(errors);
	}

}
