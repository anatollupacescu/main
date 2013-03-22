package net.parser;

public enum Predefined {
		
		CHAR(new CharacterParser()),
		
		CHAR_ARRAY(DynamicParser.newBuilder().
				start(new CharacterParser()).
				any(new CharacterParser()).build()),
				
		QUOTED_STRING(DynamicParser.newBuilder()
				.start(new AnyParser(new CharacterParser('\''), new CharacterParser('\"')))
				.any(new CharacterParser())
				.end(new CharacterParser('\'')).build());
		
		public final Parser parser;

		private Predefined(Parser parser) {
			this.parser = parser;
		}
}
