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
		return start.parse(iterator) && 
				(body != null ? body.parse(iterator) : true) && 
				(end != null ? end.parse(iterator) : true);
	}
}
