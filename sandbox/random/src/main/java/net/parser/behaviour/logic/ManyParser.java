package net.parser.behaviour.logic;

import java.util.Iterator;

import net.parser.GenericParser;
import net.parser.Parser;
import net.parser.ResetableIterator;
import net.parser.behaviour.multiplicity.MultiParser;
import net.parser.behaviour.multiplicity.SingleParser;

public class ManyParser extends GenericParser implements Parser {

	private final Parser parser;

	public ManyParser(char ch) {
		this.parser = new SingleParser(ch);
	}

	public ManyParser(Parser parser) {
		this.parser = parser;
	}

	public ManyParser(String string) {
		this.parser = new MultiParser(string);
	}

	public boolean parse(Iterator<Character> iterator) {
		ResetableIterator i = (ResetableIterator) iterator;
		int index = i.getIndex();
		while (parser.parse(i)) {
			index = i.getIndex();
		}
		i.reset(index);
		return super.parse(i);
	}

	@Override
	public String toString() {
		return "ManyParser: " + parser.toString();
	}
}
