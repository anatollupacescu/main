package net.parser;

import java.util.Iterator;

import org.apache.commons.lang3.CharUtils;

public class CharacterParser implements Parser {

	final Character character;
	
	public CharacterParser(Character c) {
		this.character = c;
	}

	public CharacterParser() {
		character = null;
	}

	public boolean parse(Iterator<Character> iterator) {
		Character inputChar = iterator.next();
		return character != null ? character.equals(inputChar) : CharUtils.isAsciiAlpha(inputChar.charValue());
	}
}
