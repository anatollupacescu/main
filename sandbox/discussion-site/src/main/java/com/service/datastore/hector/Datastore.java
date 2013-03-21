package com.service.datastore.hector;

import java.util.Map;

import me.prettyprint.cassandra.serializers.StringSerializer;

import com.service.IDatastore;

public class Datastore extends DatastoreBase implements IDatastore {

	private static final class holder {
		private static final Datastore datastore = new Datastore();
	}
	
	public static final Datastore getInstance() {
		return holder.datastore;
	}
	
	public void store(String table, String column, String key, String value) {
		insert(table, column, key, value, StringSerializer.get());
	}

	public <K> void store(String table, Map<String, Map<String, String>> keyColValues) {
		insertMulti(table, keyColValues, StringSerializer.get());
	}
	
	public <K> void store(String table, String key, Map<String, String> colValues) {
		insertMulti(table, key, colValues, StringSerializer.get());
	}
	
	public void delete(String table, String column, String[] keys) {
		delete(table, StringSerializer.get(), column, keys);
	}
	
	/*get*/
	public String get(String table, String column, String key) {
		return get(table, key, StringSerializer.get(), column);
	}
	
	public Map<String, String> get(String table, String key) {
		return getMulti(table, StringSerializer.get(), key);
	}
	
	public <K> Map<String, String> get(String table, String column, String[] keys) {
		return getMulti(table, StringSerializer.get(), column, keys);
	}
	
	public <K> Map<String, Map<String, String>> get(String table, String[] column, String[] keys) {
		return getMulti(table, StringSerializer.get(), column, keys);
	}
	
}
