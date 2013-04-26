package net.map;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.Maps;
import com.google.common.collect.Table.Cell;

public class MapTransformer {

	private final MapData data;
	private final Map<String, Object> returnMap = Maps.newHashMap();

	public MapTransformer(String propertiesFile, String... dataFiles)
			throws JsonParseException, JsonMappingException, IOException {
		data = new MapData(propertiesFile, dataFiles);
	}

	public MapTransformer(String propertiesFile, Map<String, Object> map) {
		data = new MapData(propertiesFile, map);
	}

	public Map<String, Object> getMap() {
		if(returnMap.size() > 0) {
			return returnMap;
		}
		Iterator<Cell<String, String, String[]>> transformations = data.iterator();
		while (transformations.hasNext()) {
			Cell<String, String, String[]> entry = transformations.next();
			String key = entry.getRowKey();
			String op = entry.getColumnKey();
			if (Operation.copy.toString().equals(op)) {
				insert(key, data.extract(entry.getValue()[0]));
			} else if (Operation.concat.toString().equals(op)) {
				insert(key, concat(entry.getValue()));
			} else if (Operation.merge.toString().equals(op)) {
				insert(key, merge(entry.getValue(), false));
			} else if (Operation.update.toString().equals(op)) {
				insert(key, merge(entry.getValue(), true));
			}
		}
		return returnMap;
	}

	@SuppressWarnings("unchecked")
	private void insert(String key, Object inputValue) {
		String[] path = key.split("\\.");
		if (path.length == 0) {
			path = new String[] { key };
		}
		Object value = null;
		String currentKey = null;
		Map<String, Object> prevContainer = null;
		Map<String, Object> currentContainer = returnMap;
		for (String string : path) {
			currentKey = string;
			value = currentContainer.get(currentKey);
			if (value == null) {
				value = new HashMap<String, Object>();
				prevContainer = currentContainer;
				currentContainer.put(currentKey, value);
				currentContainer = (Map<String, Object>) value;
			}
		}
		prevContainer.put(currentKey, inputValue);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object merge(String[] args, boolean b) {
		String[] ids = Arrays.copyOfRange(args, 2, args.length);
		List<String> keys = Arrays.asList(ids);
		Object left = data.extract(args[0]);
		if (left instanceof Collection) {
			List<Map> rightList = (List) data.extract(args[1]);
			for (Map map : (List<Map>) left) {
				for (Map innerMap : rightList) {
					boolean match = true;
					for (String key : keys) {
						Object mapKey = map.get(key);
						if (mapKey != null && !mapKey.equals(innerMap.get(key))) {
							match = false;
							break;
						}
					}
					if (match) {
						mergeMaps(map, innerMap, keys, b);
					}
				}
			}
		} else if (left instanceof Map) {
			Map right = (Map) data.extract(args[1]);
			mergeMaps((Map) left, right, keys, b);
		} else {
			throw new RuntimeException("Unsupported type");
		}
		return left;
	}

	private void mergeMaps(Map<String, Object> existing,
			Map<String, Object> incoming, List<String> list, boolean override) {
		if (existing == null || incoming == null) {
			throw new IllegalArgumentException("Can not merge null maps");
		}
		for (Entry<String, Object> entry : existing.entrySet()) {
			Object key = entry.getKey();
			if (!list.contains(key)) {
				Object inputValue = incoming.get(key);
				if (override || inputValue != null) {
					entry.setValue(inputValue);
				}
			}
		}
	}

	private String concat(String[] args) {
		String delim = args[0];
		StringBuilder sb = new StringBuilder();
		int i = 1;
		for (; i < args.length - 1; i++) {
			String value = (String) data.extract(args[i]);
			sb.append(value + delim);
		}
		sb.append((String) data.extract(args[i]));
		return sb.toString();
	}
}
