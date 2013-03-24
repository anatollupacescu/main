package net.parser.behaviour;

import java.util.Iterator;

import net.parser.GenericParser;
import net.parser.Parser;
import net.parser.predicate.CharPredicate;

import com.google.common.collect.ImmutableSet;

public class AnyParser extends GenericParser implements Parser {

	private final ImmutableSet<Parser> parsers;
	
	public AnyParser(char[] chars) {
		ImmutableSet.Builder<Parser> builder = new ImmutableSet.Builder<Parser>();
		for(char ch : chars) {
			Parser parser = new SingleParser(new CharPredicate(ch));
			builder.add(parser);
		}
		this.parsers = builder.build();
	}
	
	public AnyParser(Parser[] parsers) {
		ImmutableSet.Builder<Parser> builder = new ImmutableSet.Builder<Parser>();
		for(Parser parser : parsers) {
			builder.add(parser);
		}
		this.parsers = builder.build();
	}

	public boolean parse(Iterator<Character> iterator) {
		boolean result = false;
		for (Parser p : parsers) {
			if (p.parse(iterator)) {
				result = true;
				break;
			}
		}
		return result && super.parse(iterator);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Parser p : parsers) {
			sb.append(p.toString());
		}
		return sb.toString();
	}
}
