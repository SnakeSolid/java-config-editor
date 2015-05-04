package ru.snake.config.model.data;

import java.util.Comparator;

public class ProblemItemComparator implements Comparator<ProblemItem> {

	@Override
	public int compare(ProblemItem left, ProblemItem right) {
		int result;

		result = right.getLevel().compareTo(left.getLevel());

		if (result != 0) {
			return result;
		}

		result = right.getPath().compareTo(left.getPath());

		return result;
	}

}
