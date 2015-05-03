package ru.snake.config.listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

public class GotoErrorListener extends MouseAdapter implements MouseListener {

	private final JTable table;
	private final int columnIndex;
	private final JTextComponent textComponent;

	public GotoErrorListener(JTable table, int columnIndex,
			JTextComponent textComponent) {
		this.table = table;
		this.columnIndex = columnIndex;
		this.textComponent = textComponent;
	}

	public void mouseClicked(MouseEvent event) {
		if (event.getClickCount() == 1) {
			return;
		}

		if (SwingUtilities.isLeftMouseButton(event)) {
			int rowIndex = table.getSelectedRow();

			if (rowIndex == -1) {
				return;
			}

			String category = (String) table.getValueAt(rowIndex, columnIndex);

			String text = textComponent.getText();
			int position = text.indexOf(category);

			if (position != -1) {
				textComponent.setCaretPosition(position);
				textComponent.requestFocusInWindow();
			}
		}
	}

}
