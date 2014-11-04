package my.nosql.datastore.model;

import java.util.HashMap;
import java.util.Map;

public class Entity {

	private final String key;
	
	private String type;
	private Map<String, String> map;

	public Entity(final String key) {
		this.key = key;
		map = new HashMap<String, String>();
	}

	public Entity(final String key, final String type, final Map<String, String> columns) {
		this.key = key;
		this.type = type;
		map = new HashMap<String,String>(columns);
	}
	
	public void add(String name, String value) {
		if(name == null || value == null) return;
		map.put(name, value);
	}

	public String get(String column) {
		return map.get(column);
	}	

	public String getKey() {
		return key;
	}

	public String getType() {
		return type;
	}	
	
	public void addAll(Map<String, String> transaction) {
		map.putAll(transaction);
	}
	
	public Map<String,String> getMap() {
		return map;
	}
	
	@Override
	public String toString() {
		return "Entity [key=" + key + ", type=" + getType() + ", columns=" + map + "]";
	}
}
