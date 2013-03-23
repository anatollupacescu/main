package net.parser.behaviour;

import java.util.Iterator;

import net.parser.GenericParser;
import net.parser.Parser;

import com.google.common.base.Predicate;

public class SingleParser extends GenericParser implements Parser  {
	
	public final Predicate<Character> predicate;
	
	public SingleParser(Predicate<Character> p) {
		predicate = p;
	}

	@Override
	public boolean parse(Iterator<Character> i) {
		sanitizeIterator(i);
		return predicate.apply(i.next()) ? super.parse(i) : false;
	}
	
	@Override
	public String toString() {
		return "[SingleParser for " + predicate.toString() + "]";
	}
}
