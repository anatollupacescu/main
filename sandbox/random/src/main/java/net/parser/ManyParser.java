package net.parser;

import java.util.Iterator;

public class ManyParser implements Parser {

	private final Parser parser;
	
	public ManyParser(Parser parser) {
		this.parser = parser;
	}

	public boolean parse(Iterator<Character> iterator) {
		while(parser.parse(iterator));
		return true;
	}
}
