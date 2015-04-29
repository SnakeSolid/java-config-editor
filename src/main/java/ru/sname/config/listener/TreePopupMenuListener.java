package ru.sname.config.listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

public class TreePopupMenuListener extends MouseAdapter implements
		MouseListener {

	private final JTree tree;
	private final JPopupMenu popup;

	public TreePopupMenuListener(JTree tree, JPopupMenu popup) {
		this.tree = tree;
		this.popup = popup;
	}

	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			int row = tree.getClosestRowForLocation(e.getX(), e.getY());
			tree.setSelectionRow(row);

			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}

}
