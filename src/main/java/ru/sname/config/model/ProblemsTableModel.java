package ru.sname.config.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import ru.sname.config.syntax.error.SyntaxError;
import ru.sname.config.util.ProblemItem;

@SuppressWarnings("serial")
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProblemsTableModel extends AbstractTableModel implements
		TableModel {

	private static final String[] COLUMN_NAMES = new String[] { "Description",
			"Path", "Location" };
	private static final Class<?>[] COLUMN_TYPES = new Class<?>[] {
			String.class, String.class, String.class };

	private final List<ProblemItem> items;

	public ProblemsTableModel() {
		this.items = new ArrayList<ProblemItem>();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return COLUMN_TYPES[columnIndex];
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return COLUMN_NAMES[columnIndex];
	}

	@Override
	public int getRowCount() {
		return items.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return items.get(rowIndex).getDescription();
		} else if (columnIndex == 1) {
			return items.get(rowIndex).getPath();
		} else if (columnIndex == 2) {
			return items.get(rowIndex).getLocation();
		}

		return null;
	}

	public void setDetails(Collection<SyntaxError> errors) {
		int oldCount = items.size();

		items.clear();

		for (SyntaxError error : errors) {
			ProblemItem item = new ProblemItem();
			item.setDescription(error.toString());
			item.setPath(error.getPath());
			item.setLocation(error.getLocation());

			items.add(item);
		}

		int newCount = items.size();

		fireRowsChanged(oldCount, newCount);
	}

	private void fireRowsChanged(int oldCount, int newCount) {
		if (oldCount == 0 && newCount == 0) {
		} else if (oldCount == 0 && newCount > 0) {
			fireTableRowsInserted(0, newCount - 1);
		} else if (oldCount > 0 && newCount == 0) {
			fireTableRowsDeleted(0, oldCount - 1);
		} else if (oldCount > newCount) {
			fireTableRowsUpdated(0, newCount - 1);
			fireTableRowsDeleted(newCount, oldCount - 1);
		} else if (oldCount == newCount) {
			fireTableRowsUpdated(0, newCount - 1);
		} else if (oldCount < newCount) {
			fireTableRowsUpdated(0, oldCount - 1);
			fireTableRowsInserted(oldCount, newCount - 1);
		}
	}

}
