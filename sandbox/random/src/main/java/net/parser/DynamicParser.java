package net.parser;

import java.util.Iterator;

import net.parser.behaviour.AnyParser;
import net.parser.behaviour.ManyParser;
import net.parser.behaviour.SingleParser;
import net.parser.predicate.CharPredicate;

public class DynamicParser {

	private final GenericParser parser;
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
	
	public boolean parse(Iterator<Character> iterator) {
		boolean result = parser.parse(iterator);
		if(result && !acceptTrash && iterator.hasNext()) {
			throw new IllegalStateException("Trash found after the end of parsed entity: " + iterator);
		}
		return result; 
	}
	
	public static class DynamicParserBuilder {
		
		private final boolean acceptTrash;
		
		private GenericParser parser;
		private GenericParser delegate;
		
		public DynamicParserBuilder(boolean flag) {
			acceptTrash = flag;
		}

		public DynamicParserBuilder() {
			acceptTrash = true;
		}

		public DynamicParserBuilder one(char c) {
			addParser(new SingleParser(new CharPredicate(c)));
			return this;
		}

		public DynamicParserBuilder one(char... chars) {
			addParser(new AnyParser(chars));
			return this;
		}

		public DynamicParserBuilder many(char c) {
			addParser(new ManyParser(c));
			return this;
		}
		
		private void addParser(GenericParser p) {
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

		public GenericParser getParser() {
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
