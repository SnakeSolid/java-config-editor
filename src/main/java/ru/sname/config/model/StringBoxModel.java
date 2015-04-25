package ru.sname.config.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@SuppressWarnings("serial")
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StringBoxModel extends AbstractListModel<String> implements
		ComboBoxModel<String> {

	private final List<String> items;

	private String selected;

	public StringBoxModel() {
		this.items = new ArrayList<String>();
		this.selected = null;
	}

	@Override
	public String getElementAt(int index) {
		return items.get(index);
	}

	@Override
	public int getSize() {
		return items.size();
	}

	@Override
	public Object getSelectedItem() {
		return selected;
	}

	@Override
	public void setSelectedItem(Object anItem) {
		if (anItem == null) {
			selected = null;
		} else {
			selected = String.valueOf(anItem);
		}

		fireContentsChanged(this, -1, -1);
	}

	public void setList(Collection<? extends String> list) {
		clear();

		addAll(list);

		selected = null;
	}

	protected void addAll(Collection<? extends String> list) {
		int size;

		items.addAll(list);

		size = items.size();

		if (size > 0) {
			fireIntervalAdded(this, 0, size - 1);
		}
	}

	public void clear() {
		int size;

		size = items.size();

		if (size > 0) {
			items.clear();

			fireIntervalRemoved(this, 0, size - 1);
		}
	}

}
