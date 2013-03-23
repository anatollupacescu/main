package net.parser;

import java.util.Iterator;

public class ParserUtils {

	public static Iterator<Character> stringIterator(final String string) {
		if (string == null) {
			throw new NullPointerException();
		}
		return new ResetableIterator(string);
	}
}
