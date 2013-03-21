package net.parser;

import java.util.Iterator;

public class DynamicParser implements Parser {

	private final Parser start;
	private final Parser body;
	private final Parser end;
	
	public DynamicParser(DynamicParserBuilder parserBuilder) {
		this.start = parserBuilder.getStart();
		this.body = parserBuilder.getBody();
		this.end = parserBuilder.getEnd();
	}

	public static DynamicParserBuilder newBuilder() {
		return new DynamicParserBuilder();
	}
	
	public boolean parse(String content) {
		Iterator<Character> iterator = ParserUtils.stringIterator(content);
		return parse(iterator);
	}
	
	public boolean parse(Iterator<Character> iterator) {
		return ensureStart(iterator) && ensureBody(iterator) && ensureEnd(iterator);
	}
	
	private boolean ensureStart(Iterator<Character> iterator) {
		return start.parse(iterator);
	}

	private boolean ensureBody(Iterator<Character> iterator) {
		if (body == null) {
			return body.parse(iterator);
		}
		return true;
	}

	private boolean ensureEnd(Iterator<Character> iterator) {
		if (end == null) {
			return end.parse(iterator);
		}
		return true;
	}
}
