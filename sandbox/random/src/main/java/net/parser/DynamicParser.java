package net.parser;

import java.util.Iterator;

import net.parser.behaviour.AnyParser;
import net.parser.behaviour.ManyParser;
import net.parser.type.AlphaParser;

public class DynamicParser extends Parser {

	private final Parser parser;
	
	public DynamicParser(DynamicParserBuilder parserBuilder) {
		this.parser = parserBuilder.getParser();
	}

	public static DynamicParserBuilder newBuilder() {
		return new DynamicParserBuilder();
	}
	
	public boolean parse(String content) {
		Iterator<Character> iterator = ParserUtils.stringIterator(content);
		return parse(iterator);
	}
	
	public boolean parse(Iterator<Character> iterator) {
		boolean result = parser.parse(iterator);
		if(iterator.hasNext()) {
			throw new IllegalStateException("Trash found after the end of parsed entity: " + iterator);
		}
		return result; 
	}
	
	public static class DynamicParserBuilder {
		
		private Parser parser;
		private Parser delegate;
		
		public DynamicParserBuilder one(Predefined predefined) {
			addParser(predefined.getParser());
			return this;
		}
		
		public DynamicParserBuilder one(char c) {
			Parser newParser = new AlphaParser(c);
			addParser(newParser);
			return this;
		}
		
		public DynamicParserBuilder one(String string) {
			addParser(new AlphaParser(string));
			return this;
		}
		
		public DynamicParserBuilder one(String ... string) {
			addParser(new AnyParser(string));
			return this;
		}
		
		public DynamicParserBuilder one(Parser parser) {
			addParser(parser);
			return this;
		}
		
		public DynamicParserBuilder start(Parser ... parser) {
			addParser(new AnyParser(parser));
			return this;
		}

		/*many*/
		
		public DynamicParserBuilder many(char c) {
			addParser(new ManyParser(new AlphaParser(c)));
			return this;
		}

		public DynamicParserBuilder many(String string) {
			addParser(new ManyParser(new AlphaParser(string)));
			return this;
		}

		public DynamicParserBuilder many(Parser parser) {
			addParser(new ManyParser(parser));
			return this;
		}
		
		private void addParser(Parser newParser) {
			if(parser == null) {
				parser = newParser;
			} else if (delegate == null){
				parser.setDelegate(newParser);
				delegate = newParser;
			} else {
				delegate.setDelegate(newParser);
				delegate = newParser;
			}
		}
		
		public Parser getParser() {
			return parser;
		}
		
		public DynamicParser build() {
			return new DynamicParser(this);
		}
	}
	
	@Override
	public String toString() {
		return parser.toString();
	}
}
