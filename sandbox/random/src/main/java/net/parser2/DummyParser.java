package net.parser2;

import java.util.Queue;

import net.parser2.core.CharIterator;
import net.parser2.core.CharParser;
import net.parser2.core.Parser;
import net.parser2.core.StringParser;
import net.parser2.core.behaviour.AnyOf;
import net.parser2.core.behaviour.Many;

import com.google.common.collect.Lists;

public class DummyParser implements Parser {

	private final String name;
	private final Queue<Parser> parts;
	
	public DummyParser(ParserBuilder builder) {
		name = builder.getName();
		parts = builder.getParts();
	}
	
	public String getName() {
		return name;
	}
	
	public void parse(String input) {
		CharIterator i = new CharIterator(input);
		this.parse(i);
	}
	
	@Override
	public boolean parse(CharIterator i) {
		while(!parts.isEmpty()) {
			Parser parser = parts.poll();
			if(!parser.parse(i)) {
				throw new IllegalArgumentException("Could not parse " + i.peek());
			}
		}
		return true;
	}

	public static ParserBuilder builder() {
		return new ParserBuilder();
	}
	
	public static class ParserBuilder {

		private String name;
		private final Queue<Parser> parts = Lists.newLinkedList();
		
		public ParserBuilder name(String string) {
			this.name = string;
			return this;
		}
		
		public Queue<Parser> getParts() {
			return parts;
		}
		public ParserBuilder one(char c) {
			parts.offer(new CharParser(c));
			return this;
		}
		public ParserBuilder one(char... cs) {
			parts.offer(new AnyOf(cs));
			return this;
		}
		public ParserBuilder one(String string) {
			parts.offer(new StringParser(string));
			return this;
		}
		public ParserBuilder one(String... string) {
			parts.offer(new AnyOf(string));
			return this;
		}
		public ParserBuilder one(DummyParser dummyParser) {
			parts.offer(dummyParser);
			return this;
		}
		public ParserBuilder one(DummyParser... parser) {
			parts.offer(new AnyOf(parser));
			return this;
		}

		//--------------
		public ParserBuilder many(char c) {
			Many many = new Many(c);
			parts.offer(many);
			return this;
		}
		public ParserBuilder many(String string) {
			Many many = new Many(string);
			parts.offer(many);
			return this;
		}
		public ParserBuilder many(Parser parser) {
			Many many = new Many(parser);
			parts.offer(many);
			return this;
		}
		public ParserBuilder many() {
			Many many = new Many();
			parts.offer(many);
			return this;
		}
		
		public String getName() {
			return name;
		}
		public DummyParser build() {
			return new DummyParser(this);
		}
	}
}
