package ru.sname.config.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.text.Document;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ru.sname.config.worker.util.NodeDescriptor;

@SuppressWarnings("serial")
public class TreeGotoAction extends ActionAdapter {

	private final JTree tree;
	private final JTextPane text;

	public TreeGotoAction(JTree tree, JTextPane text) {
		setName("Go to node");
		setDescription("Do to selected node in editor.");
		setMnemonic(KeyEvent.VK_G);

		this.tree = tree;
		this.text = text;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		TreeSelectionModel model = tree.getSelectionModel();
		TreePath path = model.getSelectionPath();
		DefaultMutableTreeNode child = (DefaultMutableTreeNode) path
				.getLastPathComponent();
		NodeDescriptor descriptor = (NodeDescriptor) child.getUserObject();
		Document document = text.getDocument();

		int offset = descriptor.getOffset();
		int length = document.getLength();

		if (offset > length) {
			offset = length;
		}

		text.setCaretPosition(offset);
		text.requestFocusInWindow();
	}

}
