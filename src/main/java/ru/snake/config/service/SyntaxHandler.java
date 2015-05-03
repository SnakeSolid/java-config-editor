package ru.snake.config.service;

import java.util.Collection;

import ru.snake.config.syntax.ComponentEntry;

public interface SyntaxHandler {

	public void handleComponent(ComponentEntry entry);

	public void handleComponents(Collection<ComponentEntry> entries);

}
