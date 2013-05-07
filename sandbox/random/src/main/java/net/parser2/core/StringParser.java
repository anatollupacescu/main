package net.parser2.core;

import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

public class StringParser implements Parser {

	private final List<Predicate<Character>> predicates;
	
	public StringParser(CharSequence sequence) {
		ImmutableList.Builder<Predicate<Character>> builder = new ImmutableList.Builder<Predicate<Character>>();
		for(int i = 0; i < sequence.length(); i++) {
			char ch = sequence.charAt(i);
			CharPredicate predicate = new CharPredicate(ch);
			builder.add(predicate);
		}
		predicates = builder.build();
	}

	@Override
	public boolean parse(CharIterator i) {
		if(!i.hasNext()) {
			return false;
		}
		for(Predicate<Character> predicate : predicates) {
			if (predicate.apply(i.peek())) {
				i.next(); 
			} else {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(Predicate<Character> p : predicates) {
			builder.append(p.toString());
			builder.append(", ");
		}
		return "[StringParser for " + builder.toString() + "]";
	}
}
