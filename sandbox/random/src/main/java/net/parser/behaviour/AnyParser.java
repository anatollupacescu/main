package net.parser.behaviour;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.google.common.base.Predicate;

import net.parser.GenericParser;
import net.parser.predicate.CharPredicate;

public class AnyParser extends GenericParser {

	public final Collection<Predicate<Character>> predicates;
	
	public AnyParser(char[] chars) {
		predicates = new LinkedList<Predicate<Character>>();
		for(char ch : chars) {
			Predicate<Character> p = new CharPredicate(ch);
			predicates.add(p);
		}
	}
	
	public boolean parse(Iterator<Character> iterator) {
		sanitizeIterator(iterator);
		boolean result = false;
		char ch = iterator.next();
		for(Predicate<Character> p : predicates) {
			if(p.apply(ch)) {
				result = true;
				break;
			}
		}
		return result && super.parse(iterator);
	}
}
