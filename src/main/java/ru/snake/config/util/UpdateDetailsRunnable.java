package ru.snake.config.util;

import ru.snake.config.model.DetailsTableModel;

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
