package net.parser;

import static org.junit.Assert.*;
import org.junit.*;

public class ParserTest {

	@Test
	public void testChar() {
		DynamicParser parser = DynamicParser.newBuilder().one('c').one('b').many('c').many('z').build();
		assertTrue(parser.parse("cb"));
		assertTrue(parser.parse("cbc"));
		assertTrue(parser.parse("cbccc"));
		assertFalse(parser.parse("ccc"));
		assertTrue(parser.parse("cb"));
		assertTrue(parser.parse("cbzzz"));
		assertTrue(parser.parse("cbccc"));
	}
	
	/*TODO dea adaugat inca un constructor pentru Character pe linga String ':' vs ":"*/
}
