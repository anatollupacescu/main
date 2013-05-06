package net.parser2;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

public class MultiParser implements IParser {

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
		return "[MultiParser for " + builder.toString() + "]";
	}
}
