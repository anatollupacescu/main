package net.map;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class Merger2Test {

	private final String PROPERTIES = "merge.properties";
	private final String PROPERTIES2 = "merge2.properties";
	
	@Test
	public void test1() throws JsonParseException, JsonMappingException, IOException {
		MapTransformer m = new MapTransformer(PROPERTIES, "data.json");
		System.out.println(m.getMap());
		MapTransformer m2 = new MapTransformer(PROPERTIES2, m.getMap());
		System.out.println(m2.getMap());
	}
}
