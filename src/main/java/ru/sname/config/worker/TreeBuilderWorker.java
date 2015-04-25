package ru.sname.config.worker;

import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import ru.sname.config.worker.util.NodeDescriptor;

public class TreeBuilderWorker extends SwingWorker<Void, Void> {

	private DefaultTreeModel treeModel;
	private String content;
	private DefaultMutableTreeNode root;
	private Collection<String> pathes;

	@Override
	protected Void doInBackground() throws Exception {
		root = (DefaultMutableTreeNode) treeModel.getRoot();
		pathes = new LinkedList<String>();

		unmarkNodes(root);

		if (content == null) {
			root = null;

			return null;
		}

		if (content.length() == 0) {
			root = null;

			return null;
		}

		try (Scanner scanner = new Scanner(content)) {
			if (isCancelled()) {
				return null;
			}

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				if (line.startsWith("[/") && line.endsWith("]")) {
					String path = line.substring(2, line.length() - 1);

					pathes.add(path);
				}
			}
		}

		return null;
	}

	private void unmarkNodes(DefaultMutableTreeNode node) {
		@SuppressWarnings("unchecked")
		Enumeration<DefaultMutableTreeNode> it = node.children();

		while (it.hasMoreElements()) {
			DefaultMutableTreeNode child = it.nextElement();
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

		for (String path : pathes) {
			addChild(path);
		}

		if (root != null) {
			removeNodes(root);
		}
	}

	private void removeNodes(DefaultMutableTreeNode node) {
		@SuppressWarnings("unchecked")
		Enumeration<DefaultMutableTreeNode> it = node.children();

		while (it.hasMoreElements()) {
			DefaultMutableTreeNode child = it.nextElement();
			NodeDescriptor descriptor = (NodeDescriptor) child.getUserObject();

			if (!descriptor.isMark()) {
				node.remove(child);

				treeModel.reload(node);
			} else {
				removeNodes(child);
			}
		}
	}

	private void addChild(String line) {
		DefaultMutableTreeNode node = root;

		try (Scanner scanner = new Scanner(line)) {
			scanner.useDelimiter("/");

			while (scanner.hasNext()) {
				String name = scanner.next();
				boolean found = false;

				@SuppressWarnings("unchecked")
				Enumeration<DefaultMutableTreeNode> it = node.children();

				while (it.hasMoreElements()) {
					DefaultMutableTreeNode child = it.nextElement();
					NodeDescriptor descriptor = (NodeDescriptor) child
							.getUserObject();
					String nodeName = descriptor.getName();

					if (name.equals(nodeName)) {
						descriptor.setMark(true);

						node = child;

						found = true;

						break;
					}
				}

				if (!found) {
					NodeDescriptor descriptor = new NodeDescriptor(name);
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

	private void insertChild(DefaultMutableTreeNode node,
			DefaultMutableTreeNode newChild, String newChildName) {
		@SuppressWarnings("unchecked")
		Enumeration<DefaultMutableTreeNode> it = node.children();
		int position = 0;

		while (it.hasMoreElements()) {
			DefaultMutableTreeNode child = it.nextElement();
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
