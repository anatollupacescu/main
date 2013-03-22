package net.parser;

import java.util.Iterator;

public class ManyParser implements Parser {

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
}
