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
public class TreeSelectAction extends ActionAdapter {

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

		DefaultMutableTreeNode next = child.getNextSibling();
		text.setSelectionStart(offset);

		if (next != null) {
			NodeDescriptor nextDescriptor = (NodeDescriptor) next
					.getUserObject();
			int nextOffset = nextDescriptor.getOffset();

			if (nextOffset > length) {
				nextOffset = length;
			}

			text.setSelectionEnd(nextOffset);
		} else {
			text.setSelectionEnd(length);
		}

		text.requestFocusInWindow();
	}

}
