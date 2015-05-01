package ru.sname.config.worker.util;

import ru.sname.config.model.DetailsTableModel;

public class UpdateDetailsRunnable implements Runnable {

	private DetailsTableModel model;
	private DetatilsDescriptor descriptor;

	public void setModel(DetailsTableModel model) {
		this.model = model;
	}

	public void setDescriptor(DetatilsDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	@Override
	public void run() {
		model.setDetails(descriptor);
	}

}
