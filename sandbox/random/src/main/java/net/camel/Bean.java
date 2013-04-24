package net.camel;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//@JsonIgnoreProperties(ignoreUnknown = true)
public class Bean {

	private Map<String,String>[] entries;
	
	public Bean convert(String input) throws JsonParseException, JsonMappingException, IOException {
		Object obj = new ObjectMapper(new JsonFactory()).readValue(input, List.class);
		
		return null;
	}
	
	public Map<String, String>[] getEntries() {
		return entries;
	}

	public void setEntries(Map<String, String>[] entries) {
		this.entries = entries;
	}

	public void print(Bean input) {
		System.out.println("[print] ");
	}
}
