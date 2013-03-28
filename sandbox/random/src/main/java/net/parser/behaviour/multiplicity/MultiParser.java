package net.parser.behaviour.multiplicity;

import java.util.Iterator;

import net.parser.GenericParser;
import net.parser.Parser;
import net.parser.ResetableIterator;
import net.parser.predicate.CharPredicate;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

public class MultiParser extends GenericParser implements Parser {

	private final ImmutableList<Predicate<Character>> predicates;
	
	public MultiParser(CharSequence sequence) {
		ImmutableList.Builder<Predicate<Character>> builder = new ImmutableList.Builder<Predicate<Character>>();
		for(int i = 0; i < sequence.length(); i++) {
			char ch = sequence.charAt(i);
			CharPredicate predicate = new CharPredicate(ch);
			builder.add(predicate);
		}
		predicates = builder.build();
	}

	@Override
	public boolean parse(Iterator<Character> i) {
		if(!i.hasNext()) {
			return false;
		}
		for(Predicate<Character> predicate : predicates) {
			if (predicate.apply(((ResetableIterator)i).peek())) {
				i.next(); 
			} else {
				return false;
			}
		}
		return super.parse(i);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(Predicate<Character> p : predicates) {
			builder.append(p.toString());
			builder.append(", ");
		}
		return "[MultiParser for " + builder.toString() + "]";
	}
}
