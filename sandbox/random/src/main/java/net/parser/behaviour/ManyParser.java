package net.parser.behaviour;

import java.util.Iterator;

import net.parser.GenericParser;
import net.parser.Parser;
import net.parser.ResetableIterator;

public class ManyParser extends GenericParser implements Parser {

	private final Parser parser;
	
	public ManyParser(char ch) {
		this.parser = new SingleParser(ch);
	}
	
	public ManyParser(Parser parser) {
		this.parser = parser;
	}
	
	public boolean parse(Iterator<Character> iterator) {
		ResetableIterator i = (ResetableIterator)iterator;
		int index = i.getIndex();
		while(parser.parse(i)) {
			index = i.getIndex();
		}
		if(i.hasNext()) {
			i.reset(index);
			return super.parse(i);
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "ManyParser: " + parser.toString();
	}
}
