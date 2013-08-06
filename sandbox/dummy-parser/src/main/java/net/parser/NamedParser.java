package net.parser;

import java.util.Map;
import java.util.Queue;

public class NamedParser implements Parser {

	private String name;
	private Queue<Parser> parsers;

	public NamedParser(ParserBuilder parserBuilder) {
	}

	public Map<String,String> parse(String input) {
		return null;
	}
	
	public static ParserBuilder newBuilder(String string) {
		return new ParserBuilder(string);
	}

	public static ParserBuilder newBuilder() {
		return new ParserBuilder(null);
	}
	
	public static class ParserBuilder {

		private String name;
		private Queue<Parser> parsers;
		
		public ParserBuilder(String string) {
			this.name = string;
		}

		public String getName() {
			return name;
		}

		public ParserBuilder zeroOrOne(String string) {
			return this;
		}

		public ParserBuilder zeroOrOne(Parser one) {
			return this;
		}

		public ParserBuilder one(String... string) {
			return this;
		}

		public ParserBuilder one(Parser one) {
			return this;
		}
		
		public ParserBuilder any(String alphaNumeric) {
			return this;
		}
		
		public ParserBuilder any(Parser parser) {
			return this;
		}

		public ParserBuilder oneOrMore(String any) {
			return this;
		}
		
		public ParserBuilder oneOrMore(Parser any) {
			return this;
		}
		
		public Parser build() {
			return new NamedParser(this);
		}
	}
}
