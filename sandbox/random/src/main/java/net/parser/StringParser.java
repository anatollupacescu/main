package net.parser;

import java.util.Iterator;

import org.apache.commons.lang3.ArrayUtils;

public class StringParser implements Parser {

	final Character[] characters;
	
	public StringParser(String string) {
		this.characters = ArrayUtils.toObject(string.toCharArray());
	}

	public boolean parse(Iterator<Character> iterator) {
		for(Character c : characters) {
			if (!c.equals(iterator.next())) {
				return false;
			}
		}
		return true;
	}
}
