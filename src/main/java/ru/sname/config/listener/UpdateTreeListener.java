package ru.sname.config.listener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.tree.DefaultTreeModel;

import ru.sname.config.worker.TreeBuilderWorker;

public class UpdateTreeListener implements DocumentListener {

	private TreeBuilderWorker treeBuilder;
	private DefaultTreeModel treeModel;

	public UpdateTreeListener(DefaultTreeModel treeModel) {
		this.treeModel = treeModel;
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		Document document = e.getDocument();

		startTreeBuilder(document);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		Document document = e.getDocument();

		startTreeBuilder(document);
	}

	private void startTreeBuilder(Document document) {
		int length = document.getLength();
		String content;

		try {
			content = document.getText(0, length);
		} catch (BadLocationException e) {
			return;
		}

		if (treeBuilder != null) {
			if (!treeBuilder.isCancelled()) {
				treeBuilder.cancel(true);
			}
		}

		treeBuilder = new TreeBuilderWorker();
		treeBuilder.setModel(treeModel);
		treeBuilder.setContent(content);
		treeBuilder.execute();
	}

}
