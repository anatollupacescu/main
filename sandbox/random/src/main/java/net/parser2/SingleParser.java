package net.parser2;

import com.google.common.base.Predicate;

public class SingleParser implements IParser  {
	
	private final Predicate<Character> predicate;
	private boolean ignore = false;

	public void ignore() {
		ignore = true;
	}
	
	public SingleParser(Predicate<Character> p) {
		predicate = p;
	}

	public SingleParser(char c) {
		predicate = new CharPredicate(c);
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
		return ignore;
	}
	
	@Override
	public String toString() {
		return "[SingleParser for " + predicate.toString() + "]";
	}
}
