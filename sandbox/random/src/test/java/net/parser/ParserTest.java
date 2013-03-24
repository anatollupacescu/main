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

	@Test
	public void testChar2() {
		Parser inner = DynamicParser.newBuilder().one('b').many('c').build();
		DynamicParser parser = DynamicParser.newBuilder().one('a').one(inner).build();
		assertTrue(parser.parse("ab"));
		assertTrue(parser.parse("abcc"));
		assertFalse(parser.parse("accc"));
		assertFalse(parser.parse("bccc"));
	}
	/*TODO dea adaugat inca un constructor pentru Character pe linga String ':' vs ":"*/
}
