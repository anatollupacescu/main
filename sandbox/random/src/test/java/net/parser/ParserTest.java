package net.parser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static net.parser.Predefined.*;

import org.junit.Ignore;
import org.junit.Test;

public class ParserTest {

	private final static String content = "{\"numele\" : valera}";
	
	@Test
	public void testPredefined() {
		/*char parser*/
		Parser charParser = CHAR.parser;
		assertTrue(charParser.parse(ParserUtils.stringIterator("c")));
		assertFalse(charParser.parse(ParserUtils.stringIterator("1")));
		assertFalse(charParser.parse(ParserUtils.stringIterator("cc")));
		DynamicParser dynamicCharParser = DynamicParser.newBuilder().start(Predefined.CHAR).build();
		assertTrue(dynamicCharParser.parse(ParserUtils.stringIterator("c")));
		assertFalse(dynamicCharParser.parse(ParserUtils.stringIterator("1")));
		
		/*string parser*/
		Parser charArrayParser = CHAR_ARRAY.parser;
		assertTrue(charArrayParser.parse(ParserUtils.stringIterator("c")));
		assertTrue(charArrayParser.parse(ParserUtils.stringIterator("cd")));
		assertTrue(charArrayParser.parse(ParserUtils.stringIterator("cdf")));
	}
	
	@Test
	@Ignore
	public void test() {
		Parser key = DynamicParser.newBuilder().start(Predefined.QUOTED_STRING).end(":").build();
		Parser keyValue = DynamicParser.newBuilder().start(key).end(Predefined.CHAR_ARRAY).build();
		DynamicParser parser = DynamicParser.newBuilder().start("{").body(keyValue).end("}").build();
		assertTrue(parser.parse(content));
	}
	
	/*TODO dea adaugat inca un constructor pentru Character pe linga String ':' vs ":"*/
}
