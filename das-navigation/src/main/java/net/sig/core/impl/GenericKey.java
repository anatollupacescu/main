package net.sig.core.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericKey extends HashMap<String, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> keyNames = Collections.emptyList();
	
	public List<String> getKeyNames() {
		return keyNames;
	}

	public GenericKey(List<String> keyNames) {
		this.keyNames = keyNames;
	}

	/**
	 * creates a consistent key
	 * 
	 * @param keyNames
	 * @param of
	 */
	public GenericKey(List<String> keyNames, Map<String, String> of) {
		this.keyNames = keyNames;
		for(String keyName : keyNames) {
			String keyValue = of.get(keyName);
			if(keyValue == null) {
				throw new IllegalArgumentException(String.format("Could not infer key value for '%s' from incoming map '%s'", keyName, of.toString()));
			}
			put(keyName, keyValue);
		}
	}
	
	/**
	 * may result in inconsistent keys
	 * 
	 * @param of
	 */
	public void inferValues(Map<String, String> of) {
		for(String keyName : keyNames) {
			String keyValue = of.get(keyName);
			if(keyValue != null) {
				put(keyName, keyValue);
			}
		}
	}
	
	public void inferValues(Map<String, String> of, Map<String, String> translationMap) {
		for(String keyName : keyNames) {
			String translatedKey = translationMap.get(keyName);
			String keyValue = of.get(translatedKey);
			if(keyValue != null) {
				if(get(keyName) == null) {
					put(keyName, keyValue);
				}
			}
		}
	}
	
	public boolean isIncompleteKey() {
		return keyNames.size() > values().size();
	}

	@Override
	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}
		if(!(o instanceof GenericKey)) {
			return false;
		}
		GenericKey other = (GenericKey)o;
		if(other.keyNames.size() != keyNames.size()) {
			return false;
		}
		for(String keyName : keyNames) {
			if( ! get(keyName).equals(other.get(keyName))) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{ ");
		for(String keyName : keyNames) {
			sb.append(keyName + "=" + get(keyName));
		}
		sb.append(" }");
		return sb.toString();
	}

}
