package ru.snake.config.service;

import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import ru.snake.config.syntax.AttributeEntry;
import ru.snake.config.syntax.ColumnEntry;
import ru.snake.config.syntax.ComponentEntry;
import ru.snake.config.syntax.SubcomponentEntry;
import ru.snake.config.syntax.error.AttributeRequiredError;
import ru.snake.config.syntax.error.InvalidCategoryError;
import ru.snake.config.syntax.error.InvalidValueError;
import ru.snake.config.syntax.error.SubcomponentRequiredError;
import ru.snake.config.syntax.error.SyntaxError;
import ru.snake.config.syntax.error.TooManyColumnsError;
import ru.snake.config.syntax.error.TooManyValuesError;
import ru.snake.config.syntax.error.UnusedAttributeError;
import ru.snake.config.syntax.error.UnusedSubcomponentError;
import ru.snake.config.tree.ConfigNode;
import ru.snake.config.util.Patterns;

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
					AttributeRequiredError error = new AttributeRequiredError();
					error.setAttribute(attributeName);
					error.setPath(getPath(node));
					error.setLocation(node.getName());

					result.add(error);
				}
			}

			// Check for miltivalued attributes
			for (AttributeEntry entry : component.getAttributes()) {
				if (entry.isMultiValued()) {
					continue;
				}

				String attributeName = entry.getName();

				if (node.getValues(attributeName).size() > 1) {
					TooManyValuesError error = new TooManyValuesError();
					error.setAttribute(attributeName);
					error.setPath(getPath(node));
					error.setLocation(node.getName());

					result.add(error);
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
					UnusedAttributeError error = new UnusedAttributeError();
					error.setAttribute(attributeName);
					error.setPath(getPath(node));
					error.setLocation(node.getName());

					result.add(error);
				}
			}

			// Check attribute values
			for (AttributeEntry entry : component.getAttributes()) {
				String attributeName = entry.getName();

				for (String valueList : node.getValues(entry.getName())) {
					Iterator<ColumnEntry> it = entry.getColumns().iterator();

					try (Scanner scanner = new Scanner(valueList)) {
						scanner.useDelimiter(Patterns.COLUMN_SEPARATOR);

						while (scanner.hasNext()) {
							ColumnEntry column;

							if (it.hasNext()) {
								column = it.next();
							} else {
								TooManyColumnsError error = new TooManyColumnsError();
								error.setAttribute(attributeName);
								error.setPath(getPath(node));
								error.setLocation(node.getName());

								result.add(error);

								break;
							}

							String value = scanner.next();
							String type = column.getType();
							Collection<String> validValues = column.getValues();

							if (!validValues.isEmpty()) {
								if (!validValues.contains(value)) {
									InvalidValueError error = new InvalidValueError();
									error.setAttribute(attributeName);
									error.setType(type);
									error.setValue(value);
									error.setPath(getPath(node));
									error.setLocation(node.getName());

									result.add(error);

									continue;
								}
							}

							if (type.equals("Boolean")) {
								if (value.equals("true")
										|| value.equals("false")) {
									; // it is boolean
								} else {
									InvalidValueError error = new InvalidValueError();
									error.setAttribute(attributeName);
									error.setType(type);
									error.setValue(value);
									error.setPath(getPath(node));
									error.setLocation(node.getName());

									result.add(error);
								}
							} else if (type.equals("Integer")) {
								try {
									Integer.parseInt(value);
								} catch (NumberFormatException e) {
									InvalidValueError error = new InvalidValueError();
									error.setAttribute(attributeName);
									error.setType(type);
									error.setValue(value);
									error.setPath(getPath(node));
									error.setLocation(node.getName());

									result.add(error);
								}
							} else if (type.equals("Long")) {
								try {
									Long.parseLong(value);
								} catch (NumberFormatException e) {
									InvalidValueError error = new InvalidValueError();
									error.setAttribute(attributeName);
									error.setType(type);
									error.setValue(value);
									error.setPath(getPath(node));
									error.setLocation(node.getName());

									result.add(error);
								}
							} else if (type.equals("Float")) {
								try {
									Float.parseFloat(value);
								} catch (NumberFormatException e) {
									InvalidValueError error = new InvalidValueError();
									error.setAttribute(attributeName);
									error.setType(type);
									error.setValue(value);
									error.setPath(getPath(node));
									error.setLocation(node.getName());

									result.add(error);
								}
							} else if (type.equals("IPAddress")) {
								Matcher matcher = Patterns.IP_ADDRESS
										.matcher(value);

								if (!matcher.matches()) {
									InvalidValueError error = new InvalidValueError();
									error.setAttribute(attributeName);
									error.setType(type);
									error.setValue(value);
									error.setPath(getPath(node));
									error.setLocation(node.getName());

									result.add(error);
								}
							} else if (type.equals("ClassName")) {
								int index = value.lastIndexOf('.');
								String valueClass = value.substring(index + 1);

								if (!components.containsKey(valueClass)) {
									InvalidValueError error = new InvalidValueError();
									error.setAttribute(attributeName);
									error.setType(type);
									error.setValue(value);
									error.setPath(getPath(node));
									error.setLocation(node.getName());

									result.add(error);
								}
							} else if (type.equals("ConfigEntry")) {
								if (!node.hasChild(value)) {
									InvalidValueError error = new InvalidValueError();
									error.setAttribute(attributeName);
									error.setType(type);
									error.setValue(value);
									error.setPath(getPath(node));
									error.setLocation(node.getName());

									result.add(error);
								}
							} else if (type.equals("NMEAttribute")) {
								; // do not checked now
							}
						}
					}
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
					SubcomponentRequiredError error = new SubcomponentRequiredError();
					error.setSubcomponent(entryName);
					error.setPath(getPath(node));
					error.setLocation(node.getName());

					result.add(error);
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
						InvalidCategoryError error = new InvalidCategoryError();
						error.setSubcomponent(entryName);
						error.setGivenCategory(componentCategory);
						error.setExpectedCategory(category);
						error.setPath(getPath(node));
						error.setLocation(node.getName());

						result.add(error);
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

				// need check attributes for using this node
				found = true;

				if (!found) {
					UnusedSubcomponentError error = new UnusedSubcomponentError();
					error.setSubcomponent(childName);
					error.setPath(getPath(node));
					error.setLocation(node.getName());

					result.add(error);
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
				TooManyValuesError error = new TooManyValuesError();
				error.setAttribute(CLASS_NAME_KEY);
				error.setPath(getPath(node));
				error.setLocation(node.getName());

				result.add(error);
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

		if (!elements.isEmpty()) {
			elements.removeLast();
		}

		StringBuilder builder = new StringBuilder();
		Iterator<String> it = elements.descendingIterator();

		builder.append('[');

		while (it.hasNext()) {
			String element = it.next();

			builder.append('/');
			builder.append(element);
		}

		builder.append(']');

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
