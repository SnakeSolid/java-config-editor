package ru.sname.config.worker;

import java.util.Collection;
import java.util.LinkedList;

import ru.sname.config.model.ProblemsTableModel;
import ru.sname.config.service.SyntaxService;
import ru.sname.config.syntax.error.SyntaxError;
import ru.sname.config.tree.ConfigNode;
import ru.sname.config.tree.TreeParser;

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
