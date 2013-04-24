package net.map;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public class Merger {

	private final String key = "key";

	@SuppressWarnings("unchecked")
	public void go(Map<String, Object> map1, Map<String, Object> map2) {
		for (Entry<String, Object> entry : map1.entrySet()) {
			Object value1 = map1.get(entry.getKey());
			Object value2 = map2.get(entry.getKey());
			if (value1 instanceof Map) {
				go((Map<String, Object>) value1, (Map<String, Object>) value2);
			} else if (value1 instanceof Collection) {
				mergeCollections((Collection<Object>) value1, (Collection<Object>) value2);
			} else {
				entry.setValue(value2);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void mergeCollections(Collection<Object> value1, Collection<Object> value2) {
		if (value2 instanceof Collection) {
			Object[] values1 = value1.toArray();
			Object[] values2 = value2.toArray();
			for (int i = 0; i < values2.length; i++) {
				if (values2[i] instanceof Map) {
					Map<String, Object> map = (Map<String, Object>) values2[i];
					Object key1 = map.get(key);
					if (key1 == null) {
						throw new IllegalArgumentException("Key not found");
					}
					for (int j = 0; j < values1.length; j++) {
						Map<String, Object> map2 = (Map<String, Object>) values1[j];
						if (key1.equals(map2.get(key))) {
							go(map, map2);
							break;
						}
					}
				} else {
					throw new IllegalArgumentException("Only collections of object are suported");
				}
			}
		} else {
			throw new IllegalArgumentException("Only collections mer");
		}
	}
}
