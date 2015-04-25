package ru.sname.config.model;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProblemsTableModel extends AbstractTableModel implements
		TableModel {

	private static final long serialVersionUID = -7923520463621341127L;

	private static final String[] COLUMN_NAMES = new String[] { "Description",
			"Path", "Location" };
	private static final Class<?>[] COLUMN_TYPES = new Class<?>[] {
			String.class, String.class, String.class };

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
		return 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
	}

}
