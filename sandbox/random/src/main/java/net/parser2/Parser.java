package net.parser2;

import java.util.Queue;

import com.google.common.collect.Lists;

public class Parser implements IParser {

	private final String name;
	private final Queue<IParser> parts;
	
	public Parser(ParserBuilder builder) {
		name = builder.getName();
		parts = builder.getParts();
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public boolean parse(CharIterator i) {
		while(!parts.isEmpty()) {
			IParser parser = parts.poll();
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
		private final Queue<IParser> parts = Lists.newLinkedList();
		
		public ParserBuilder name(String string) {
			this.name = string;
			return this;
		}
		
		public Queue<IParser> getParts() {
			return parts;
		}
		public ParserBuilder one(char c) {
			parts.offer(new SingleParser(c));
			return this;
		}
		public ParserBuilder one(char... cs) {
			parts.offer(new AnyParser(cs));
			return this;
		}
		public ParserBuilder one(String string) {
			parts.offer(new MultiParser(string));
			return this;
		}
		public ParserBuilder one(String... string) {
			parts.offer(new AnyParser(string));
			return this;
		}
		public ParserBuilder one(Parser parser) {
			parts.offer(parser);
			return this;
		}
		public ParserBuilder one(Parser... parser) {
			parts.offer(new AnyParser(parser));
			return this;
		}
		
		//--------------
		public ParserBuilder maybe(char c) {
			SingleParser single = new SingleParser(c);
			single.ignore();
			parts.offer(single);
			return this;
		}
		public ParserBuilder maybe(char... cs) {
			IParser anyParser = new AnyParser(cs);
			parts.offer(anyParser);
			return this;
		}
		public ParserBuilder maybe(String string) {
			return ;
		}
		public ParserBuilder maybe(String... string) {
			return this;
		}
		public ParserBuilder maybe(Parser parser) {
			return this;
		}
		public ParserBuilder maybe(Parser... parser) {
			return this;
		}
		
		//--------------
		public ParserBuilder many(char c) {
			return this;
		}
		public ParserBuilder many(char... c) {
			return this;
		}
		public ParserBuilder many(String string) {
			return this;
		}
		public ParserBuilder many(String... string) {
			return this;
		}
		public ParserBuilder many(Parser parser) {
			return this;
		}
		public ParserBuilder many(Parser... parser) {
			return this;
		}
		public String getName() {
			return name;
		}
		public Parser build() {
			return new Parser(this);
		}
	}
}
