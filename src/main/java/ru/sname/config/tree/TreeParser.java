package ru.sname.config.tree;

import java.util.Scanner;

import ru.sname.config.worker.util.Patterns;

public class TreeParser {

	private final String content;

	public TreeParser(String content) {
		this.content = content;
	}

	public static ConfigNode parse(String content) {
		return new TreeParser(content).parse();
	}

	private ConfigNode parse() {
		ConfigNode root = new ConfigNode("");
		ConfigNode node = root;

		try (Scanner scanner = new Scanner(content)) {
			scanner.useDelimiter(Patterns.LINE);

			while (scanner.hasNext()) {
				String line = scanner.next();

				if (line.startsWith("#")) {
					// skip comment
				} else if (line.startsWith("[/") && line.endsWith("]")) {
					String path = line.substring(2, line.length() - 1);

					node = buildCathegory(root, path);
				} else {
					scanAttribute(node, line);
				}
			}
		}

		return root;
	}

	private void scanAttribute(ConfigNode node, String line) {
		int index = line.indexOf('=');

		if (index == -1) {
			return;
		} else if (index == 0) {
			return;
		}

		String attribute = line.substring(0, index);
		String value = line.substring(index + 1);

		node.pushValue(attribute, value);
	}

	private ConfigNode buildCathegory(ConfigNode root, String line) {
		ConfigNode node = root;

		try (Scanner scanner = new Scanner(line)) {
			scanner.useDelimiter(Patterns.CATEGORY_DELIMITER);

			while (scanner.hasNext()) {
				String name = scanner.next();

				if (node.hasChild(name)) {
					node = node.getChild(name);
				} else {
					ConfigNode child = new ConfigNode(name);

					node.addChild(child);

					node = child;
				}
			}
		}

		return node;
	}
}
