package ru.snake.config.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DetatilsDescriptor {

	private Map<String, Date> dates;
	private Map<String, String> strings;
	private Map<String, Long> longs;

	public DetatilsDescriptor() {
		dates = new HashMap<String, Date>();
		strings = new HashMap<String, String>();
		longs = new HashMap<String, Long>();
	}

	public void putDate(String key, long value) {
		dates.put(key, new Date(value));
	}

	public void putString(String key, String value) {
		strings.put(key, value);
	}

	public void putLong(String key, long value) {
		longs.put(key, value);
	}

	public List<String> getDateKeys() {
		ArrayList<String> result = new ArrayList<>();

		for (Entry<String, Date> entry : dates.entrySet()) {
			result.add(entry.getKey());
		}

		Collections.sort(result);

		return result;
	}

	public List<String> getStringKeys() {
		ArrayList<String> result = new ArrayList<>();

		for (Entry<String, String> entry : strings.entrySet()) {
			result.add(entry.getKey());
		}

		Collections.sort(result);

		return result;
	}

	public List<String> getLongKeys() {
		ArrayList<String> result = new ArrayList<>();

		for (Entry<String, Long> entry : longs.entrySet()) {
			result.add(entry.getKey());
		}

		Collections.sort(result);

		return result;
	}

	public Date getDate(String key) {
		return dates.get(key);
	}

	public String getString(String key) {
		return strings.get(key);
	}

	public Long getLong(String key) {
		return longs.get(key);
	}

}
