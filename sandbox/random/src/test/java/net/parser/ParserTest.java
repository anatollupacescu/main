package net.parser;

import static org.junit.Assert.*;
import org.junit.Test;

public class ParserTest {

	private final static String content = "{\"numele\" : valera}";
	
	@Test
	public void test() {
		Parser key = DynamicParser.newBuilder().start(Predefined.QUOTED_STRING).end(":").build();
		Parser keyValue = DynamicParser.newBuilder().start(key).end(Predefined.CHAR_ARRAY).build();
		DynamicParser parser = DynamicParser.newBuilder().start("{").body(keyValue).end("}").build();
		assertTrue(parser.parse(content));
	}
	
	/*TODO dea adaugat inca un constructor pentru Character pe linga String ':' vs ":"*/
}
