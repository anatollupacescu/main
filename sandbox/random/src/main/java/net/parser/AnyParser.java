package net.parser;

import java.util.Iterator;

import com.google.common.collect.ImmutableList;

public class AnyParser implements Parser {

	final ImmutableList<Parser> parsers;
	
	public AnyParser(String[] string) {
		ImmutableList.Builder<Parser> builder = new ImmutableList.Builder<Parser>();
		for(String s : string) {
			builder.add(new StringParser(s));
		}
		parsers = builder.build();
	}

	public AnyParser(DynamicParser[] parser) {
		ImmutableList.Builder<Parser> builder = new ImmutableList.Builder<Parser>();
		for(Parser p : parser) {
			builder.add(p);
		}
		parsers = builder.build();
	}

	public boolean parse(Iterator<Character> iterator) {
		// TODO Auto-generated method stub
		return false;
	}
}
