package net.parser2;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.CharUtils;

import com.google.common.base.Predicate;

public class CharPredicate implements Predicate<Character> {

	private final static Logger logger = Logger.getAnonymousLogger();
	
	private final Character character;
	
	public CharPredicate(Character ch) {
		this.character = ch;
	}
	
	@Override
	public boolean apply(Character ch) {
		if(ch == null) {
			throw new IllegalArgumentException("Input character can not be null");
		}
		boolean result;
		if(character == null) {
			result = CharUtils.isAsciiAlpha(ch);
			logger.log(Level.INFO, "Any alpha accepted. {0} is alpha: {1}", new Object[] { ch, result });
			return result;
		}
		result = character.equals(ch);
		logger.log(Level.INFO, "Expected character [{0}]  got [{1}] returning {2}", new Object[] { character, ch, result });
		return result;
	}
	
	@Override
	public String toString() {
		return "[CharPredicate for " + character + "]";
	}
}
