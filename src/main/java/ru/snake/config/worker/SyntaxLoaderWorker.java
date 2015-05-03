package ru.snake.config.worker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.snake.config.service.SyntaxHandler;
import ru.snake.config.syntax.AttributeEntry;
import ru.snake.config.syntax.ComponentEntry;
import ru.snake.config.syntax.SubcomponentEntry;
import ru.snake.config.tree.ConfigNode;
import ru.snake.config.tree.TreeParser;

public class SyntaxLoaderWorker extends AbstractConfigWorker {

	private static final Logger logger = LoggerFactory
			.getLogger(SyntaxLoaderWorker.class);

	private static final String SYNTAX_DIRECTORY = "spell";

	private SyntaxHandler handler;

	public void setHandler(SyntaxHandler handler) {
		this.handler = handler;
	}

	@Override
	protected Void doInBackground() throws Exception {
		for (File syntaxFile : new File(SYNTAX_DIRECTORY).listFiles()) {
			ConfigNode root = null;

			try (InputStream stream = new FileInputStream(syntaxFile)) {
				root = TreeParser.parse(stream);
			} catch (IOException e) {
				logger.warn("Unable to initialize spelling", e);
			}

			if (root == null) {
				continue;
			}

			LinkedList<ComponentEntry> components = new LinkedList<>();

			for (ConfigNode child : root.getChildren()) {
				ComponentEntry component = new ComponentEntry();
				component.setClassName(child.getValue("ClassName"));
				component.setPackageName(child.getValue("PackageName"));
				component.setCategory(child.getValue("Category"));

				for (String subcomponentName : child.getValues("Subcomponent")) {
					ConfigNode subcomponent = child.getChild(subcomponentName);
					String name = subcomponent.getValue("Name");
					String category = subcomponent.getValue("Category");
					String required = subcomponent.getValue("Required");

					SubcomponentEntry entry = new SubcomponentEntry();
					entry.setName(name);
					entry.setCategory(category);
					entry.setRequired(Boolean.parseBoolean(required));

					component.addSubcomponent(entry);
				}

				for (String subcomponentName : child.getValues("Attribute")) {
					ConfigNode subcomponent = child.getChild(subcomponentName);
					String name = subcomponent.getValue("Name");
					String required = subcomponent.getValue("Required");
					String multi = subcomponent.getValue("MultiValued");

					AttributeEntry entry = new AttributeEntry();
					entry.setName(name);
					entry.setRequired(Boolean.parseBoolean(required));
					entry.setMultiValued(Boolean.parseBoolean(multi));

					component.addAttribute(entry);
				}

				components.add(component);
			}

			handler.handleComponents(components);
		}

		return null;
	}

}
