package ru.snake.config.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import ru.snake.config.syntax.AttributeEntry;
import ru.snake.config.syntax.ComponentEntry;
import ru.snake.config.syntax.SubcomponentEntry;
import ru.snake.config.syntax.error.AttributeRequiredError;
import ru.snake.config.syntax.error.InvalidCategoryError;
import ru.snake.config.syntax.error.SubcomponentRequiredError;
import ru.snake.config.syntax.error.SyntaxError;
import ru.snake.config.syntax.error.TooManyValuesError;
import ru.snake.config.syntax.error.UnusedAttributeError;
import ru.snake.config.syntax.error.UnusedSubcomponentError;
import ru.snake.config.tree.ConfigNode;
import ru.snake.config.tree.TreeParser;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SyntaxService {

	private static final String CLASS_NAME_KEY = "ClassName";
	private static final String SYNTAX_DIRECTORY = "spell";

	private static final Logger logger = LoggerFactory
			.getLogger(SyntaxService.class);

	private Map<String, ComponentEntry> components;

	public SyntaxService() {
		components = new HashMap<String, ComponentEntry>();
	}

	@PostConstruct
	private void inintialize() {
		components.clear();

		for (File syntaxFile : new File(SYNTAX_DIRECTORY).listFiles()) {
			ConfigNode root = null;

			try (InputStream stream = new FileInputStream(syntaxFile)) {
				root = TreeParser.parse(stream);
			} catch (IOException e) {
				logger.warn("Unable to initialize spelling", e);
			}

			if (root == null) {
				return;
			}

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

					component.addSubcomponent(name, category,
							Boolean.parseBoolean(required));
				}

				for (String subcomponentName : child.getValues("Attribute")) {
					ConfigNode subcomponent = child.getChild(subcomponentName);
					String name = subcomponent.getValue("Name");
					String required = subcomponent.getValue("Required");
					String multi = subcomponent.getValue("MultiValued");

					component.addAttribute(name,
							Boolean.parseBoolean(required),
							Boolean.parseBoolean(multi));
				}

				components.put(component.getClassName(), component);
			}
		}
	}

	public Collection<SyntaxError> checkNode(ConfigNode node) {
		Collection<SyntaxError> result = new LinkedList<>();

		result.addAll(checkClassName(node));
		result.addAll(checkSubcomponent(node));
		result.addAll(checkAttribute(node));

		return result;
	}

	private Collection<? extends SyntaxError> checkAttribute(ConfigNode node) {
		Collection<SyntaxError> result = new LinkedList<>();

		for (String className : node.getValues(CLASS_NAME_KEY)) {
			if (!components.containsKey(className)) {
				continue;
			}

			ComponentEntry component = components.get(className);

			// Check for required attributes
			for (AttributeEntry entry : component.getAttributes()) {
				if (!entry.isRequired()) {
					continue;
				}

				String attributeName = entry.getName();

				if (!node.hasAttribute(attributeName)) {
					AttributeRequiredError descriptor = new AttributeRequiredError();
					descriptor.setAttribute(attributeName);
					descriptor.setPath(getPath(node));
					descriptor.setLocation(node.getName());

					result.add(descriptor);
				}
			}

			// Check for miltivalued attributes
			for (AttributeEntry entry : component.getAttributes()) {
				if (entry.isMultiValued()) {
					continue;
				}

				String attributeName = entry.getName();

				if (node.getValues(attributeName).size() > 1) {
					TooManyValuesError descriptor = new TooManyValuesError();
					descriptor.setAttribute(attributeName);
					descriptor.setPath(getPath(node));
					descriptor.setLocation(node.getName());

					result.add(descriptor);
				}
			}

			// Check for unused attributes
			for (String attributeName : node.getAttributes()) {
				boolean found = false;

				for (AttributeEntry entry : component.getAttributes()) {
					if (entry.getName().equals(attributeName)) {
						found = true;

						break;
					}
				}

				if (!found) {
					UnusedAttributeError descriptor = new UnusedAttributeError();
					descriptor.setAttribute(attributeName);
					descriptor.setPath(getPath(node));
					descriptor.setLocation(node.getName());

					result.add(descriptor);
				}
			}
		}

		return result;
	}

	private Collection<SyntaxError> checkSubcomponent(ConfigNode node) {
		Collection<SyntaxError> result = new LinkedList<>();

		for (String className : node.getValues(CLASS_NAME_KEY)) {
			if (!components.containsKey(className)) {
				continue;
			}

			ComponentEntry component = components.get(className);

			// Check for required subcomponenets
			for (SubcomponentEntry entry : component.getSubcomponents()) {
				if (!entry.isRequired()) {
					continue;
				}

				String entryName = entry.getName();

				if (!node.hasChild(entryName)) {
					SubcomponentRequiredError descriptor = new SubcomponentRequiredError();
					descriptor.setSubcomponent(entryName);
					descriptor.setPath(getPath(node));
					descriptor.setLocation(node.getName());

					result.add(descriptor);
				}
			}

			// Check for subcomponenet categories
			for (SubcomponentEntry entry : component.getSubcomponents()) {
				String entryName = entry.getName();

				if (components.containsKey(entryName)) {
					ComponentEntry componentEntry = components.get(entryName);
					String componentCategory = componentEntry.getCategory();
					String category = entry.getCategory();

					if (!category.equals(componentCategory)) {
						InvalidCategoryError descriptor = new InvalidCategoryError();
						descriptor.setSubcomponent(entryName);
						descriptor.setGivenCategory(componentCategory);
						descriptor.setExpectedCategory(category);
						descriptor.setPath(getPath(node));
						descriptor.setLocation(node.getName());

						result.add(descriptor);
					}
				}
			}

			// Check for unused subcomponenets
			for (ConfigNode child : node.getChildren()) {
				String childName = child.getName();
				boolean found = false;

				for (SubcomponentEntry entry : component.getSubcomponents()) {
					if (entry.getName().equals(childName)) {
						found = true;

						break;
					}
				}

				if (!found) {
					UnusedSubcomponentError descriptor = new UnusedSubcomponentError();
					descriptor.setSubcomponent(childName);
					descriptor.setPath(getPath(node));
					descriptor.setLocation(node.getName());

					result.add(descriptor);
				}
			}
		}

		return result;
	}

	private Collection<SyntaxError> checkClassName(ConfigNode node) {
		Collection<SyntaxError> result = new LinkedList<>();

		if (node.hasAttribute(CLASS_NAME_KEY)) {
			List<String> classNames = node.getValues(CLASS_NAME_KEY);

			if (classNames.size() > 1) {
				TooManyValuesError descriptor = new TooManyValuesError();
				descriptor.setAttribute(CLASS_NAME_KEY);
				descriptor.setPath(getPath(node));
				descriptor.setLocation(node.getName());

				result.add(descriptor);
			}
		}

		return result;
	}

	private String getPath(ConfigNode node) {
		Deque<String> elements = new LinkedList<String>();
		ConfigNode parent = node;

		while (parent != null) {
			elements.addLast(parent.getName());
			parent = parent.getParent();
		}

		StringBuilder builder = new StringBuilder();
		Iterator<String> it = elements.descendingIterator();

		while (it.hasNext()) {
			String element = it.next();

			builder.append('/');
			builder.append(element);
		}

		return builder.toString();
	}

}
