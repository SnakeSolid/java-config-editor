package ru.snake.config.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import ru.snake.config.syntax.error.ErrorLevel;

@SuppressWarnings("serial")
public class ErrorLevelCellRenderer extends DefaultTableCellRenderer implements
		TableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);

		ErrorLevel level = (ErrorLevel) value;

		switch (level) {
		case HINT:
			setForeground(Color.GRAY);
			setText("Hint");
			break;

		case WARNING:
			setForeground(Color.DARK_GRAY);
			setText("Warn");
			break;

		case ERROR:
			setForeground(Color.BLACK);
			setText("Error");
			break;

		default:
			setText("");
			break;
		}

		if (!isSelected) {
			switch (level) {
			case HINT:
				setBackground(Color.WHITE);
				break;

			case WARNING:
				setBackground(Color.YELLOW);
				break;

			case ERROR:
				setBackground(Color.RED);
				break;
			}
		}

		return this;
	}

}
