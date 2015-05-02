package ru.snake.config.model;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import ru.snake.config.util.DetailsItem;
import ru.snake.config.util.DetatilsDescriptor;

@SuppressWarnings("serial")
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DetailsTableModel extends AbstractTableModel implements TableModel {

	private static final String[] COLUMN_NAMES = new String[] { "Description",
			"Value" };
	private static final Class<?>[] COLUMN_TYPES = new Class<?>[] {
			String.class, String.class };

	private final List<DetailsItem> items;
	private final DateFormat dateFormat;
	private final NumberFormat numberFormat;

	public DetailsTableModel() {
		this.items = new ArrayList<DetailsItem>();
		this.dateFormat = DateFormat.getDateTimeInstance();
		this.numberFormat = NumberFormat.getIntegerInstance();
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
			return items.get(rowIndex).getKey();
		} else if (columnIndex == 1) {
			return items.get(rowIndex).getValue();
		}

		return null;
	}

	public void setDetails(DetatilsDescriptor descriptor) {
		int oldCount = items.size();

		items.clear();

		for (String keyName : descriptor.getDateKeys()) {
			Date value = descriptor.getDate(keyName);
			DetailsItem item = new DetailsItem();
			item.setKey(keyName);
			item.setValue(dateFormat.format(value));

			items.add(item);
		}

		for (String keyName : descriptor.getStringKeys()) {
			String value = descriptor.getString(keyName);
			DetailsItem item = new DetailsItem();
			item.setKey(keyName);
			item.setValue(value);

			items.add(item);
		}

		for (String keyName : descriptor.getLongKeys()) {
			Long value = descriptor.getLong(keyName);
			DetailsItem item = new DetailsItem();
			item.setKey(keyName);
			item.setValue(numberFormat.format(value));

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
