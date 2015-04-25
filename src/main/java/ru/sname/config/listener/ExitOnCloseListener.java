package ru.sname.config.listener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import org.springframework.stereotype.Component;

import ru.sname.config.Application;

@Component("exit_on_close")
public class ExitOnCloseListener extends WindowAdapter implements
		WindowListener {

	@Override
	public void windowClosing(WindowEvent e) {
		Application.exit();
	}

}
