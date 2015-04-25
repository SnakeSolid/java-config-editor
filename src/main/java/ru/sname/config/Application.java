package ru.sname.config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;

@Configuration
@ComponentScan
public class Application {

	private static final AbstractApplicationContext context;

	static {
		context = new AnnotationConfigApplicationContext(Application.class);
	}

	public static void exit() {
		if (context.isActive()) {
			context.close();
		}
	}

	public static void main(String[] args) {
		context.getBean(MainFrame.class);
	}

}
