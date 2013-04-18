package net.camel;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Bean {

	private Map<String,String>[] entries;
	
	public Map<String, String>[] getEntries() {
		return entries;
	}

	public void setEntries(Map<String, String>[] entries) {
		this.entries = entries;
	}

	public Bean convert(String input) throws JsonParseException, JsonMappingException, IOException {
	    return new ObjectMapper(new JsonFactory()).readValue(input, new TypeReference<Bean>() { });
	}
	
	public void print(Bean input) {
		System.out.println("[print] " + input.getEntries().length);
	}
}
