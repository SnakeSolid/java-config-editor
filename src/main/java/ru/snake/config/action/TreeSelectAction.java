package ru.snake.config.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.text.Document;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

@SuppressWarnings("serial")
public class TreeSelectAction extends TreeActionAdapter {

	private final JTree tree;
	private final JTextPane text;

	public TreeSelectAction(JTree tree, JTextPane text) {
		setName("Select node");
		setDescription("Select chosen node in editor.");
		setMnemonic(KeyEvent.VK_S);

		this.tree = tree;
		this.text = text;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		TreePath path = tree.getSelectionPath();

		if (path == null) {
			return;
		}

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
				.getLastPathComponent();
		Document document = text.getDocument();

		int selectionStart = getStartsFrom(node);
		int length = document.getLength();

		if (selectionStart > length) {
			selectionStart = length;
		}

		int selectionEnd = getSelectionEnd(node, getEndsWith(node), length);

		text.setSelectionStart(selectionStart);
		text.setSelectionEnd(selectionEnd);
		text.requestFocusInWindow();
	}

}
