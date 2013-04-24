package net.map;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MergerTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void test2() throws JsonParseException, JsonMappingException, IOException {
		
		InputStream locations = this.getClass().getResourceAsStream("locations.json");
		InputStream request = this.getClass().getResourceAsStream("request.json");
		
		Map<String, Object> locationsMap = new ObjectMapper(new JsonFactory()).readValue(locations, Map.class);
		Map<String, Object> requestMap = new ObjectMapper(new JsonFactory()).readValue(request, Map.class);
		
		Merger merger = new Merger();
		merger.go(locationsMap, requestMap);
		
		System.out.println(locationsMap);
	}
}
