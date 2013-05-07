package net.parser2.core;

import com.google.common.base.Predicate;

public class CharParser implements Parser {
	
	private final Predicate<Character> predicate;

	public CharParser(Predicate<Character> p) {
		predicate = p;
	}

	public CharParser(char c) {
		predicate = new CharPredicate(c);
	}

	public CharParser() {
		predicate = new CharPredicate();
	}

	@Override
	public boolean parse(CharIterator i) {
		if(!i.hasNext()) {
			return false;
		}
		if (predicate.apply(i.peek())) {
			i.next(); 
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "[CharParser for " + predicate.toString() + "]";
	}
}
