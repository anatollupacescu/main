package net.parser2.core.behaviour;

import net.parser2.core.CharIterator;
import net.parser2.core.CharParser;
import net.parser2.core.Parser;
import net.parser2.core.StringParser;

public class Many implements Parser {

	private final Parser parser;

	public Many(char ch) {
		this.parser = new CharParser(ch);
	}

	public Many(Parser dummyParser) {
		this.parser = dummyParser;
	}

	public Many(String string) {
		this.parser = new StringParser(string);
	}

	public Many() {
		this.parser = new CharParser();
	}

	@Override
	public boolean parse(CharIterator i) {
		int index = i.getIndex();
		while (parser.parse(i)) {
			index = i.getIndex();
		}
		i.reset(index);
		return true;
	}

	@Override
	public String toString() {
		return "Many: " + parser.toString();
	}
}
