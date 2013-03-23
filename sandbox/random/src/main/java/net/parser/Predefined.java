package net.parser;

import net.parser.behaviour.AnyParser;
import net.parser.type.AlphaParser;
import net.parser.type.NumericParser;

public enum Predefined {
		
		ALPHA_CHAR(new AlphaParser()),
		
		NUM_CHAR(new NumericParser()),
		
		ALPHA_ARRAY(null),

		NUM_ARRAY(null),
		
		STRING(null),
		
		QUOTED_STRING(DynamicParser.newBuilder()
				.one(new AnyParser(new AlphaParser('\''), new AlphaParser('\"')))
				.many(new AlphaParser())
				.one(new AnyParser(new AlphaParser('\''), new AlphaParser('\"'))).build());
		
		private final Parser parser;

		private Predefined(Parser parser) {
			this.parser = parser;
		}
		
		public Parser getParser() {
			return parser;
		}
		
		@Override
		public String toString() {
			return "Predefined: " + parser.toString();
		}
}
