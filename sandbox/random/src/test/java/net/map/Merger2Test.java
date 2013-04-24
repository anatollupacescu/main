package net.map;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Merger2Test {

	private final String JSON = "data.json";
	private final String PROPERTIES = "merge.properties";
	
	@SuppressWarnings("unchecked")
	@Test
	public void test1() throws JsonParseException, JsonMappingException, IOException {
		Map<String, Object> map = new ObjectMapper(new JsonFactory()).readValue(this.getClass().getResourceAsStream(JSON), Map.class);
		Merger2 m = new Merger2(map, PROPERTIES);
		System.out.println(m.getMap());
	}
}
