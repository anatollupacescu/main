package net.map;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.google.common.collect.Maps;

public class Merger2 {

	private final Map<String, Transformation> transformations = Maps.newHashMap();
	private final Map<String, Object> map;
	
	public Merger2(Map<String, Object> input, String propertiesFile) {
		this.map = input;
		final Properties props = new Properties();
    	try {
    		props.load(this.getClass().getResourceAsStream(propertiesFile));
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
    	for (Entry<Object, Object> entry : props.entrySet()) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			String[] values = ((String)value).split(";");
			Operation operation = null;
			Type type = null;
			String[] args = null;
			for (String string : values) {
				String[] pair = string.split(":");
				Opt opt = Opt.valueOf(pair[0]);
				switch (opt) {
				case type:
					type = Type.valueOf(pair[1]);
					break;
				case operation:
					operation = Operation.valueOf(pair[1]);
					break;
				case args:
					args = pair[1].split(",");
				default:
					break;
				}
			}
			Transformation transformation = new Transformation(operation,type,args);
			transformations.put((String)key, transformation);
		}
	}
	
	public Map<String, Object> getMap() {
		Map<String, Object> returnMap = Maps.newHashMap();
		for (Entry<String, Transformation> entry : transformations.entrySet()) {
			String key = entry.getKey();
			Transformation transformation = entry.getValue();
			switch (transformation.operation) {
			case copy:
				insert(returnMap, key, getValue(transformation.type, transformation.args[0]));
				break;
			case concat:
				insert(returnMap, key, concat(transformation.args));
				break;
			case merge:
				insert(returnMap, key, merge(transformation.type, transformation.args, false));
				break;
			case update:
				insert(returnMap, key, merge(transformation.type, transformation.args, true));
			default:
				break;
			}
		}
		return returnMap;
	}

	@SuppressWarnings("unchecked")
	private void insert(Map<String, Object> ret, String key, Object inputValue) {
		String[] path = key.split("\\.");
		if(path.length == 0) {
			path = new String[] { key };
		}
		Object value = null;
		String currentKey = null;
		Map<String, Object> prevContainer = null;
		Map<String, Object> currentContainer = ret;
		for (String string : path) {
			currentKey = string;
			value = currentContainer.get(currentKey);
			if(value == null) {
				value = new HashMap<String, Object>();
				prevContainer = currentContainer;
				currentContainer.put(currentKey, value);
				currentContainer = (Map<String, Object>)value;
			}
		}
		prevContainer.put(currentKey, inputValue);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object merge(Type type, String[] args, boolean b) {
		String[] ids = Arrays.copyOfRange(args, 2, args.length);
		List<String> keys = Arrays.asList(ids);
		switch (type) {
		case array:
			List<Map> leftList = (List)extract(map, args[0]);
			List<Map> rightList = (List)extract(map, args[1]);
			for (Map map : leftList) {
				for (Map innerMap : rightList) {
					boolean match = true;
					for (String key : keys) {
						Object mapKey = map.get(key);
						if(mapKey != null && !mapKey.equals(innerMap.get(key))) {
							match = false;
							break;
						}
					}
					if(match) {
						mergeMaps(map, innerMap, keys, b);
					}
				}
			}
			return leftList;
		case object:
			Map left = (Map)extract(map, args[0]);
			Map right = (Map)extract(map, args[1]);
			mergeMaps(left, right, keys, b);
			return left;
		default:
			break;
		}
		return null;
	}

	private void mergeMaps(Map<String, Object> existing, Map<String, Object> incoming, List<String> list, boolean override) {
		if(existing == null && incoming == null) {
			throw new IllegalArgumentException("Both maps are null");
		}
		if(existing == null) {
			existing = new HashMap<String, Object>();
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
		for(; i < args.length - 1; i++) {
			String value = (String)extract(map, args[i]);
			sb.append(value + delim);
		}
		sb.append((String)extract(map, args[i]));
		return sb.toString();
	}

	@SuppressWarnings({ "rawtypes" })
	private Object getValue(Type type, String key) {
		Object value = extract(map, key);
		switch(type) {
		case string:
			return String.valueOf(value);
		case bool:
			return Boolean.valueOf((String)value);
		case array:
			return (Collection)value;
		case object:
			return (Map)value;
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	private Object extract(Map map, String key) {
		String[] path = key.split("\\.");
		Object cursor = null;
		for (String string : path) {
			if (cursor == null) {
				cursor = map.get(string);
			} else {
				cursor = ((Map)cursor).get(string);
			}
		}
		return cursor;
	}
}
