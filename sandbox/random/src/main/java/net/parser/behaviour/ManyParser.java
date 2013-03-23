package net.parser.behaviour;

import java.util.Iterator;

import net.parser.Parser;
import net.parser.ResetableIterator;

public class ManyParser extends Parser {

	private final Parser parser;
	
	public ManyParser(Parser parser) {
		this.parser = parser;
	}

	public boolean parse(Iterator<Character> iterator) {
		if(parser.parse(iterator)) {
			ResetableIterator resetable = (ResetableIterator)iterator;
			while(true) {
				int index = resetable.getIndex();
				boolean result = parser.parse(iterator);
				if (!result) {
					resetable.reset(index);
					return true;
				}
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "ManyParser: " + parser.toString();
	}
}
