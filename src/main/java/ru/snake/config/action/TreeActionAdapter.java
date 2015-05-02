package ru.snake.config.action;

import javax.swing.tree.DefaultMutableTreeNode;

import ru.snake.config.util.NodeDescriptor;

@SuppressWarnings("serial")
public abstract class TreeActionAdapter extends ActionAdapter {

	protected int getStartsFrom(DefaultMutableTreeNode node) {
		if (node == null) {
			return 0;
		}

		NodeDescriptor descriptor = (NodeDescriptor) node.getUserObject();

		if (descriptor == null) {
			return 0;
		}

		return descriptor.getStartsFrom();
	}

	protected int getEndsWith(DefaultMutableTreeNode node) {
		if (node == null) {
			return 0;
		}

		NodeDescriptor descriptor = (NodeDescriptor) node.getUserObject();

		if (descriptor == null) {
			return 0;
		}

		return descriptor.getEndsWith();
	}

	protected int getSelectionEnd(DefaultMutableTreeNode node,
			int selectionStart, int documentLength) {
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node
				.getParent();

		if (parent == null) {
			return documentLength;
		}

		DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent
				.getFirstChild();
		int selectionEnd = documentLength;
		boolean found = false;

		while (child != null) {
			int childOffset = getStartsFrom(child);

			if (childOffset > selectionStart && childOffset < selectionEnd) {
				selectionEnd = childOffset;

				found = true;
			}

			child = (DefaultMutableTreeNode) parent.getChildAfter(child);
		}

		if (found) {
			return selectionEnd;
		}

		return getSelectionEnd(parent, selectionStart, documentLength);
	}

}
