package net.parser.type;

import java.util.Iterator;

import net.parser.Parser;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.CharUtils;

public class AlphaParser extends Parser {

	final Character[] characters;
	
	public AlphaParser(Character c) {
		this.characters = new Character[] { c };
	}
	
	public AlphaParser(String string) {
		this.characters = ArrayUtils.toObject(string.toCharArray());
	}

	public AlphaParser() {
		this.characters = null;
	}
	
	public boolean parse(Iterator<Character> iterator) {
		if (!iterator.hasNext()) {
			return false;
		}
		if (characters == null) {
			if (!CharUtils.isAsciiAlpha(iterator.next())) {
				return false;
			}
			return true;
		}
		for(Character c : characters) {
			if (!c.equals(iterator.next())) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		return String.valueOf(characters);
	}
}
