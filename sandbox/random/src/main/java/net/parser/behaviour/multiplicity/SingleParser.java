package net.parser.behaviour.multiplicity;

import java.util.Iterator;

import net.parser.GenericParser;
import net.parser.Parser;
import net.parser.ResetableIterator;
import net.parser.predicate.CharPredicate;

import com.google.common.base.Predicate;

public class SingleParser extends GenericParser implements Parser  {
	
	private final Predicate<Character> predicate;
	
	public SingleParser(Predicate<Character> p) {
		predicate = p;
	}

	public SingleParser(char c) {
		predicate = new CharPredicate(c);
	}

	@Override
	public boolean parse(Iterator<Character> i) {
		if(!i.hasNext()) {
			return false;
		}
		if (predicate.apply(((ResetableIterator)i).peek())) {
			i.next(); 
			return super.parse(i);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "[CharParser for " + predicate.toString() + "]";
	}
}
