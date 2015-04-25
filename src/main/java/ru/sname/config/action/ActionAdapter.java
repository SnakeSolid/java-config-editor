package ru.sname.config.action;

import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

public abstract class ActionAdapter extends AbstractAction {

	private static final long serialVersionUID = 5572493447767935716L;

	protected void setAccelerator(int keyChar) {
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(keyChar, 0));
	}

	protected void setAccelerator(int keyChar, int modifiers) {
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(keyChar, modifiers));
	}

	protected void setMnemonic(int keyChar) {
		putValue(MNEMONIC_KEY, keyChar);
	}

	protected void setName(String name) {
		putValue(NAME, name);
	}

	protected void setDescription(String description) {
		putValue(AbstractAction.SHORT_DESCRIPTION, description);
	}

	protected void setIcon(String iconName) {
		putValue(LARGE_ICON_KEY, loadIcon(iconName));
	}

	protected Icon loadIcon(String iconName) {
		StringBuilder builder = new StringBuilder();
		builder.append("icons/");
		builder.append(iconName);
		builder.append(".png");

		ClassLoader loader = ClassLoader.getSystemClassLoader();
		URL stream = loader.getResource(builder.toString());
		ImageIcon icon = new ImageIcon(stream);

		return icon;
	}

}
