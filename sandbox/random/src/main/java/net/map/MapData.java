package net.map;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

public class MapData {

	private final Table<String, Operation, String[]> transformations = HashBasedTable.create();
	private final Map<String, Object> data;

	public MapData(String propertiesFile, Map<String, Object> map) throws IOException {
		this.data = map;
		loadProperties(propertiesFile);
	}
	
	Iterator<Cell<String, Operation, String[]>> iterator() {
		return transformations.cellSet().iterator();
	}
	
	@SuppressWarnings("rawtypes")
	Object extract(String key) {
		String[] path = key.split("\\.");
		Object cursor = null;
		for (String string : path) {
			if (cursor == null) {
				cursor = data.get(string);
			} else {
				cursor = ((Map)cursor).get(string);
			}
		}
		return cursor;
	}
	
	@SuppressWarnings("unchecked")
	public MapData(String propertiesFile, String ... dataFiles) throws JsonParseException, JsonMappingException, IOException {
    	Builder<String, Object> builder = ImmutableMap.builder();
    	for (String file : dataFiles) {
    		Map<String, Object> map = new ObjectMapper(new JsonFactory()).readValue(this.getClass().getResourceAsStream(file), Map.class);
    		builder.put(file.substring(0, file.indexOf(".")), map);
		}
    	this.data = builder.build();
    	loadProperties(propertiesFile);
    }

	private void loadProperties(String propertiesFile) throws IOException {
		final Properties props = new Properties();
		props.load(this.getClass().getResourceAsStream(propertiesFile));
    	Iterator<Entry<Object, Object>> iterator = props.entrySet().iterator();
    	while(iterator.hasNext()) {
    		Entry<Object, Object> entry = iterator.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			String[] values = ((String)value).split(";");
			String[] args = values[1].split(",");
			if(args.length == 0) {
				args = new String[] { values[1] };
			} else if ("\\".equals(args[0])) {
				args = Arrays.copyOfRange(args, 1, args.length);
				args[0] = ",";
			} else if ("".equals(args[0])) {
				args[0] = " ";
			}
			transformations.put((String)key, Operation.valueOf(values[0]), args);
		}
	}
}
