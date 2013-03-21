package com.service;

import java.util.Map;

public interface IDatastore {
	
	public void store(String table, String column, String key, String value);

	public <K> void store(String table, Map<String, Map<String, String>> keyColValues);
	
	public <K> void store(String table, String key, Map<String, String> colValues);
	
	public void delete(String table, String column, String[] keys);
	
	public String get(String table, String column, String key);
	
	public Map<String, String> get(String table, String key);
	
	public <K> Map<String, String> get(String table, String column, String[] keys);
	
	public <K> Map<String, Map<String, String>> get(String table, String[] column, String[] keys);
	
}
