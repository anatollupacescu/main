package net.parser.behaviour;

import java.util.Iterator;

import net.parser.GenericParser;
import net.parser.ResetableIterator;
import net.parser.predicate.CharPredicate;

import com.google.common.base.Predicate;

public class ManyParser extends GenericParser {

	public final Predicate<Character> predicate;
	
	public ManyParser(char ch) {
		predicate = new CharPredicate(ch);
	}
	
	public boolean parse(Iterator<Character> iterator) {
		ResetableIterator i = (ResetableIterator)iterator;
		while(true) {
			if (!iterator.hasNext()) {
				break;
			}
			i.freeze();
			char ch = i.next();
			if(!predicate.apply(ch)) {
				i.reset();
				break;
			}
			i.forget();
		}
		return super.parse(i);
	}
	
	@Override
	public String toString() {
		return "ManyParser: " + predicate.toString();
	}
}
