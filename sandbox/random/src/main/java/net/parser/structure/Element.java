package net.parser.structure;

public enum Element {
	
	ARRAY_START('['),	/* [ */
	ARRAY_END(']'),		/* ] */
	COMPLEX_START('{'),	/* { */
	COMPLEX_END('}'),	/* } */
	
	COMMA(','),			/* , */
	QUOTS('"'),			/* " */
	CHAR('*'),			/* abc */
	COLON(':');			/* : */
	
	private final Character c;
	
	Element(Character inputChar) {
		c = inputChar;
	}
	
	public Character chr() {
		return c;
	}
}
