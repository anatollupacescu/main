package net.parser;

import static org.junit.Assert.*;
import org.junit.*;

public class ParserTest {

	@Test
	public void test1() {
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
	public void test2() {
		Parser inner = DynamicParser.newBuilder().one('b').many('c').build();
		DynamicParser parser = DynamicParser.newBuilder().one('a').one(inner).one('n').build();
		assertTrue(parser.parse("ab"));
		assertTrue(parser.parse("abcc"));
		assertFalse(parser.parse("aca"));
		assertFalse(parser.parse("accc"));
		assertFalse(parser.parse("bccc"));
		assertTrue(parser.parse("abcccn"));
	}

	@Test
	public void test3() {
		DynamicParser parser = DynamicParser.newBuilder().one('a').one('b','c','d').one('z').build();
		assertTrue(parser.parse("abz"));
		assertTrue(parser.parse("acz"));
		assertTrue(parser.parse("adz"));
		assertFalse(parser.parse("abc"));
		assertFalse(parser.parse("aba"));
		assertFalse(parser.parse("aab"));
		assertTrue(parser.parse("abzz"));
	}
	
	/*TODO dea adaugat inca un constructor pentru Character pe linga String ':' vs ":"*/
}
