package net.parser.type;

import java.util.Iterator;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.CharUtils;

import net.parser.Parser;

public class NumericParser extends Parser {

	final Character[] characters;
	
	public NumericParser(Character c) {
		this.characters = new Character[] { c };
	}
	
	public NumericParser(String string) {
		this.characters = ArrayUtils.toObject(string.toCharArray());
	}

	public NumericParser() {
		this.characters = null;
	}
	
	public boolean parse(Iterator<Character> iterator) {
		if (!iterator.hasNext()) {
			return false;
		}
		if (characters == null) {
			if (!CharUtils.isAsciiNumeric(iterator.next())) {
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
