package ru.snake.config.service;

import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
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

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SyntaxService implements SyntaxHandler {

	private static final String CLASS_NAME_KEY = "ClassName";

	@Autowired
	private WorkerExecutor executor;

	private Map<String, ComponentEntry> components;

	@PostConstruct
	private void inintialize() {
		components = new HashMap<String, ComponentEntry>();

		executor.executeSyntaxLoader(this);
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

	@Override
	public void handleComponent(ComponentEntry entry) {
		components.put(entry.getClassName(), entry);
	}

	@Override
	public void handleComponents(Collection<ComponentEntry> entries) {
		for (ComponentEntry entry : entries) {
			components.put(entry.getClassName(), entry);
		}
	}

}
