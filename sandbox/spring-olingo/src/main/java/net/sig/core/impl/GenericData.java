package net.sig.core.impl;

import java.util.HashMap;

public class GenericData extends HashMap<String, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GenericKey key;

	public GenericKey getKey() {
		return key;
	}

	public void setKey(GenericKey key) {
		this.key = key;
	}
	
	public void inferKeyValues() {
		if(key == null) {
			throw new IllegalStateException("Trying to infer key values on request entity");
		}
		for(String keyName : key.getKeyNames()) {
			key.put(keyName, this.get(keyName));
		}
	}
	
	@Override
	public String toString() {
		return key.toString() + " " + super.toString();
	}
}
