package net.parser2.core.behaviour;

import java.util.Set;

import net.parser2.DummyParser;
import net.parser2.core.CharIterator;
import net.parser2.core.CharParser;
import net.parser2.core.CharPredicate;
import net.parser2.core.Parser;
import net.parser2.core.StringParser;

import com.google.common.collect.ImmutableSet;

public class AnyOf implements Parser {

	private final Set<Parser> parsers;
	
	public AnyOf(char[] chars) {
		ImmutableSet.Builder<Parser> builder = new ImmutableSet.Builder<Parser>();
		for(char ch : chars) {
			Parser parser = new CharParser(new CharPredicate(ch));
			builder.add(parser);
		}
		this.parsers = builder.build();
	}
	
	public AnyOf(DummyParser[] parsers) {
		ImmutableSet.Builder<Parser> builder = new ImmutableSet.Builder<Parser>();
		for(Parser parser : parsers) {
			builder.add(parser);
		}
		this.parsers = builder.build();
	}

	public AnyOf(String[] strings) {
		ImmutableSet.Builder<Parser> builder = new ImmutableSet.Builder<Parser>();
		for(String string : strings) {
			builder.add(new StringParser(string));
		}
		this.parsers = builder.build();
	}

	@Override
	public boolean parse(CharIterator i) {
		boolean result = false;
		int index = 0;
		for (Parser p : parsers) {
			index = i.getIndex();
			if (p.parse(i)) {
				result = true;
				break;
			}
			i.reset(index);
		}
		return result;
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
