package ru.snake.config.worker;

import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;

import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import ru.snake.config.util.NodeDescriptor;
import ru.snake.config.util.PathDescriptor;
import ru.snake.config.util.Patterns;

public class TreeBuilderWorker extends SwingWorker<Void, Void> {

	private DefaultTreeModel treeModel;
	private String content;

	private Collection<PathDescriptor> pathes;

	@Override
	protected Void doInBackground() throws Exception {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
		pathes = new LinkedList<PathDescriptor>();

		unmarkNodes(root);

		if (content == null) {
			return null;
		}

		if (content.length() == 0) {
			return null;
		}

		Matcher matcher = Patterns.LINE.matcher(content);
		int index = 0;

		while (matcher.find(index)) {
			if (isCancelled()) {
				return null;
			}

			int start = matcher.start();
			int end = matcher.end();

			if (index < start) {
				String line = content.substring(index, start);

				if (line.startsWith("[/") && line.endsWith("]")) {
					String path = line.substring(2, line.length() - 1);
					PathDescriptor descriptor = new PathDescriptor();
					descriptor.setPath(path);
					descriptor.setOffset(index);

					pathes.add(descriptor);
				}
			}

			index = end;
		}

		return null;
	}

	private void unmarkNodes(DefaultMutableTreeNode node) {
		Enumeration<TreeNode> it = node.children();

		while (it.hasMoreElements()) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) it.nextElement();
			NodeDescriptor descriptor = (NodeDescriptor) child.getUserObject();
			descriptor.setMark(false);

			unmarkNodes(child);
		}
	}

	@Override
	protected void done() {
		if (isCancelled()) {
			return;
		}

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

		for (PathDescriptor descriptor : pathes) {
			addChild(root, descriptor);
		}

		removeNodes(root);
	}

	private void removeNodes(DefaultMutableTreeNode node) {
		Enumeration<TreeNode> it = node.children();

		while (it.hasMoreElements()) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) it.nextElement();
			NodeDescriptor descriptor = (NodeDescriptor) child.getUserObject();

			if (!descriptor.isMark()) {
				node.remove(child);

				treeModel.reload(node);
			} else {
				removeNodes(child);
			}
		}
	}

	private void addChild(DefaultMutableTreeNode root, PathDescriptor pathDescriptor) {
		DefaultMutableTreeNode node = root;

		try (Scanner scanner = new Scanner(pathDescriptor.getPath())) {
			scanner.useDelimiter("/");

			while (scanner.hasNext()) {
				String name = scanner.next();
				boolean found = false;
				Enumeration<TreeNode> it = node.children();

				while (it.hasMoreElements()) {
					DefaultMutableTreeNode child = (DefaultMutableTreeNode) it.nextElement();
					NodeDescriptor descriptor = (NodeDescriptor) child.getUserObject();
					String nodeName = descriptor.getName();

					if (name.equals(nodeName)) {
						int offset = pathDescriptor.getOffset();

						if (descriptor.isMark()) {
							if (offset < descriptor.getStartsFrom()) {
								descriptor.setStartsFrom(offset);
							}

							if (descriptor.getEndsWith() < offset) {
								descriptor.setEndsWith(offset);
							}
						} else {
							descriptor.setStartsFrom(offset);
							descriptor.setEndsWith(offset);

							descriptor.setMark(true);
						}

						node = child;
						found = true;

						break;
					}
				}

				if (!found) {
					int offset = pathDescriptor.getOffset();
					NodeDescriptor descriptor = new NodeDescriptor(name);
					descriptor.setStartsFrom(offset);
					descriptor.setEndsWith(offset);
					descriptor.setMark(true);

					DefaultMutableTreeNode child = new DefaultMutableTreeNode();
					child.setUserObject(descriptor);

					insertChild(node, child, name);

					treeModel.reload(node);

					node = child;
				}
			}
		}
	}

	private void insertChild(DefaultMutableTreeNode node, DefaultMutableTreeNode newChild, String newChildName) {
		Enumeration<TreeNode> it = node.children();
		int position = 0;

		while (it.hasMoreElements()) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) it.nextElement();
			NodeDescriptor descriptor = (NodeDescriptor) child.getUserObject();
			String nodeName = descriptor.getName();

			if (newChildName.compareTo(nodeName) < 0) {
				node.insert(newChild, position);

				return;
			}

			position++;
		}

		node.insert(newChild, position);
	}

	public void setModel(DefaultTreeModel treeModel) {
		this.treeModel = treeModel;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
