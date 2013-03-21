package net.parser;

import java.util.Iterator;

public interface Parser {
	boolean parse(Iterator<Character> iterator);
}
