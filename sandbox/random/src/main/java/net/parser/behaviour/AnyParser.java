package net.parser.behaviour;

import java.util.Iterator;

import net.parser.Parser;
import net.parser.type.AlphaParser;

import com.google.common.collect.ImmutableList;

public class AnyParser extends Parser {

	final ImmutableList<Parser> parsers;
	
	public AnyParser(String... string) {
		ImmutableList.Builder<Parser> builder = new ImmutableList.Builder<Parser>();
		for(String s : string) {
			builder.add(new AlphaParser(s));
		}
		parsers = builder.build();
	}

	public AnyParser(Parser... parser) {
		ImmutableList.Builder<Parser> builder = new ImmutableList.Builder<Parser>();
		for(Parser p : parser) {
			builder.add(p);
		}
		parsers = builder.build();
	}

	public boolean parse(Iterator<Character> iterator) {
		for (Parser parser : parsers) {
			if (parser.parse(iterator)) {
				return true;
			}
		}
		return getDelegate().parse(iterator);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Parser p : parsers) {
			sb.append(p.toString());
			sb.append(" or ");
		}
		return sb.toString();
	}
}
