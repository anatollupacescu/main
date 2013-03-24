package net.parser;

import java.util.Iterator;

import net.parser.behaviour.AnyParser;
import net.parser.behaviour.ManyParser;
import net.parser.behaviour.SingleParser;

public class DynamicParser implements Parser {

	private final Parser parser;
	private final boolean acceptTrash;
	
	public DynamicParser(DynamicParserBuilder parserBuilder) {
		this.parser = parserBuilder.getParser();
		this.acceptTrash = parserBuilder.getAcceptTrash();
	}

	public static DynamicParserBuilder newBuilder() {
		return new DynamicParserBuilder();
	}

	public static DynamicParserBuilder newBuilder(boolean flag) {
		return new DynamicParserBuilder(flag);
	}
	
	public boolean parse(String content) {
		Iterator<Character> iterator = ParserUtils.stringIterator(content);
		return parse(iterator);
	}
	
	@Override
	public boolean parse(Iterator<Character> iterator) {
		boolean result = parser.parse(iterator);
		if(result && !acceptTrash && iterator.hasNext()) {
			throw new IllegalStateException("Trash found after the end of parsed entity: " + iterator);
		}
		return result; 
	}
	
	public static class DynamicParserBuilder {
		
		private final boolean acceptTrash;
		
		private Parser parser;
		private Parser delegate;
		
		public DynamicParserBuilder(boolean flag) {
			acceptTrash = flag;
		}

		public DynamicParserBuilder() {
			acceptTrash = true;
		}

		public DynamicParserBuilder one(char c) {
			addParser(new SingleParser(c));
			return this;
		}

		public DynamicParserBuilder one(char... chars) {
			addParser(new AnyParser(chars));
			return this;
		}

		public DynamicParserBuilder one(Parser parser) {
			addParser(parser);
			return this;
		}

		public DynamicParserBuilder one(Parser... parsers) {
			addParser(new AnyParser(parsers));
			return this;
		}
		
		public DynamicParserBuilder many(char c) {
			addParser(new ManyParser(c));
			return this;
		}
		
		private void addParser(Parser p) {
			if(parser == null) {
				parser = p; 
			} else if(delegate == null) {
				parser.setDelegate(p);
				delegate = p;
			} else {
				delegate.setDelegate(p);
				delegate = p;
			}
		}
		
		public boolean getAcceptTrash() {
			return acceptTrash;
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

	@Override
	public void setDelegate(Parser parser) {
	}
}
