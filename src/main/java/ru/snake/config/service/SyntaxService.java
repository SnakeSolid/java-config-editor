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

	private static final String TYPE_STRING = "String";
	private static final String TYPE_NME_ATTRIBUTE = "NMEAttribute";
	private static final String TYPE_CLASS_NAME = "ClassName";
	private static final String TYPE_IP_ADDRESS = "IPAddress";
	private static final String TYPE_FLOAT = "Float";
	private static final String TYPE_LONG = "Long";
	private static final String TYPE_INTEGER = "Integer";
	private static final String TYPE_BOOLEAN = "Boolean";
	private static final String CATEGORY_UNDEFINED = "<undefined>";
	private static final String CONFIG_ENTRY_KEY = "ConfigEntry";
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

		if (node.hasAttribute(CLASS_NAME_KEY)) {
			List<String> classNames = node.getValues(CLASS_NAME_KEY);

			if (classNames.size() == 1) {
				String className = getClassName(classNames.iterator().next());

				if (components.containsKey(className)) {
					ComponentEntry component = components.get(className);

					result.addAll(checkRequiredAttributes(component, node));
					result.addAll(checkMultiValuedAttributes(component, node));
					result.addAll(checkUnusedAttributes(component, node));
					result.addAll(checkAttributeValues(component, node));

					result.addAll(checkRequiredSubcomponents(component, node));
					result.addAll(checkSubcomponentCategories(component, node));
					result.addAll(checkUnusedSubcomponent(component, node));
				}
			} else {
				TooManyValuesError error = new TooManyValuesError();
				error.setAttribute(CLASS_NAME_KEY);
				error.setPath(getPath(node));
				error.setLocation(node.getName());

				result.add(error);
			}
		}

		return result;
	}

	private Collection<? extends SyntaxError> checkUnusedSubcomponent(
			ComponentEntry component, ConfigNode node) {
		Collection<SyntaxError> result = new LinkedList<>();

		for (ConfigNode child : node.getChildren()) {
			String childName = child.getName();
			boolean found = false;

			for (SubcomponentEntry entry : component.getSubcomponents()) {
				if (entry.getName().equals(childName)) {
					found = true;

					break;
				}
			}

			if (found) {
				continue;
			}

			attributeLoop: for (AttributeEntry entry : component
					.getAttributes()) {
				String attributeName = entry.getName();

				if (!node.hasAttribute(attributeName)) {
					for (ColumnEntry column : entry.getColumns()) {
						String type = column.getType();

						if (!type.equals(CONFIG_ENTRY_KEY)) {
							continue;
						}

						String value = column.getDefaultValue();

						if (childName.equals(value)) {
							found = true;

							break attributeLoop;
						}
					}
				}

				for (String rowValue : node.getValues(attributeName)) {
					Iterator<ColumnEntry> iterator = entry.getColumns()
							.iterator();

					try (Scanner scanner = new Scanner(rowValue)) {
						scanner.useDelimiter(Patterns.COLUMN_SEPARATOR);

						while (scanner.hasNext()) {
							ColumnEntry column;

							if (iterator.hasNext()) {
								column = iterator.next();
							} else {
								break;
							}

							String type = column.getType();
							String value = scanner.next();

							if (!type.equals(CONFIG_ENTRY_KEY)) {
								continue;
							}

							if (childName.equals(value)) {
								found = true;

								break attributeLoop;
							}
						}
					}
				}
			}

			if (!found) {
				UnusedSubcomponentError error = new UnusedSubcomponentError();
				error.setSubcomponent(childName);
				error.setPath(getPath(node));
				error.setLocation(node.getName());

				result.add(error);
			}
		}

		return result;
	}

	private Collection<? extends SyntaxError> checkSubcomponentCategories(
			ComponentEntry component, ConfigNode node) {
		Collection<SyntaxError> result = new LinkedList<>();

		for (SubcomponentEntry entry : component.getSubcomponents()) {
			String entryName = entry.getName();

			if (!node.hasChild(entryName)) {
				continue;
			}

			ConfigNode child = node.getChild(entryName);

			if (!child.hasAttribute(CLASS_NAME_KEY)) {
				InvalidCategoryError error = new InvalidCategoryError();
				error.setSubcomponent(entryName);
				error.setGivenCategory(CATEGORY_UNDEFINED);
				error.setExpectedCategory(entry.getCategory());
				error.setPath(getPath(node));
				error.setLocation(node.getName());

				result.add(error);

				continue;
			}

			String className = getClassName(child.getValue(CLASS_NAME_KEY));

			if (components.containsKey(className)) {
				ComponentEntry componentEntry = components.get(className);
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

		return result;
	}

	private Collection<? extends SyntaxError> checkRequiredSubcomponents(
			ComponentEntry component, ConfigNode node) {
		Collection<SyntaxError> result = new LinkedList<>();

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

		return result;
	}

	private Collection<SyntaxError> checkAttributeValues(
			ComponentEntry component, ConfigNode node) {
		Collection<SyntaxError> result = new LinkedList<>();

		for (AttributeEntry entry : component.getAttributes()) {
			String attributeName = entry.getName();

			for (String rowValue : node.getValues(attributeName)) {
				Iterator<ColumnEntry> iterator = entry.getColumns().iterator();

				try (Scanner scanner = new Scanner(rowValue)) {
					scanner.useDelimiter(Patterns.COLUMN_SEPARATOR);
					boolean wasString = false;

					while (scanner.hasNext()) {
						ColumnEntry column;

						if (iterator.hasNext()) {
							column = iterator.next();
						} else if (!wasString) {
							TooManyColumnsError error = new TooManyColumnsError();
							error.setAttribute(attributeName);
							error.setPath(getPath(node));
							error.setLocation(node.getName());

							result.add(error);

							break;
						} else {
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

						wasString = false;

						if (type.equals(TYPE_BOOLEAN)) {
							if (value.equals(Boolean.TRUE.toString())
									|| value.equals(Boolean.FALSE.toString())) {
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
						} else if (type.equals(TYPE_INTEGER)) {
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
						} else if (type.equals(TYPE_LONG)) {
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
						} else if (type.equals(TYPE_FLOAT)) {
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
						} else if (type.equals(TYPE_IP_ADDRESS)) {
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
						} else if (type.equals(TYPE_CLASS_NAME)) {
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
						} else if (type.equals(CONFIG_ENTRY_KEY)) {
							if (!node.hasChild(value)) {
								InvalidValueError error = new InvalidValueError();
								error.setAttribute(attributeName);
								error.setType(type);
								error.setValue(value);
								error.setPath(getPath(node));
								error.setLocation(node.getName());

								result.add(error);
							}
						} else if (type.equals(TYPE_NME_ATTRIBUTE)) {
							; // do not checked now
						} else if (type.equals(TYPE_STRING)) {
							wasString = true;
						}
					}
				}
			}
		}

		return result;
	}

	private Collection<SyntaxError> checkUnusedAttributes(
			ComponentEntry component, ConfigNode node) {
		Collection<SyntaxError> result = new LinkedList<>();

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

		return result;
	}

	private Collection<SyntaxError> checkMultiValuedAttributes(
			ComponentEntry component, ConfigNode node) {
		Collection<SyntaxError> result = new LinkedList<>();

		for (AttributeEntry entry : component.getAttributes()) {
			if (!entry.isRequired()) {
				continue;
			}

			if (entry.isMultiValued()) {
				continue;
			}

			String attributeName = entry.getName();
			List<String> values = node.getValues(attributeName);

			if (values.size() > 1) {
				TooManyValuesError error = new TooManyValuesError();
				error.setAttribute(attributeName);
				error.setPath(getPath(node));
				error.setLocation(node.getName());

				result.add(error);
			}
		}

		return result;
	}

	private String getClassName(String value) {
		int index = value.lastIndexOf('.');

		if (index == -1) {
			return value;
		}

		return value.substring(index + 1);
	}

	private Collection<SyntaxError> checkRequiredAttributes(
			ComponentEntry component, ConfigNode node) {
		Collection<SyntaxError> result = new LinkedList<>();

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
