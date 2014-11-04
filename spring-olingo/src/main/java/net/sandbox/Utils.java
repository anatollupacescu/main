package net.sandbox;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;

public class Utils {

	private static final Pattern keyPattern = Pattern.compile("[\\(|,]([\\w]+)=(['][\\w]+[']|[\\w])");

	public static boolean hasKey(String pathComponent) {
		if (pathComponent.contains("(")) {
			int openingBracketIndex = pathComponent.indexOf("(");
			int closingBracketIndex = pathComponent.indexOf(")");
			if (closingBracketIndex < (pathComponent.length() - 1)) {
				throw new IllegalArgumentException("Content found after key");
			}
			if ((openingBracketIndex + 2) == closingBracketIndex) {
				throw new IllegalArgumentException("Key cannot be empty");
			}
			return true;
		}
		return false;
	}

	public static String[] separateKey(String pathComponent) {
		final int openingBracketIndex = pathComponent.indexOf("(");
		return new String[] {	/*entity set name*/ pathComponent.substring(0, openingBracketIndex), 
								/*key part*/ pathComponent.substring(openingBracketIndex) 
				};
	}

	public static Map<String, String> extractKeyMap(final String pathComponent) {
		int openingBrackets = pathComponent.indexOf("(");
		int closingBrackets = pathComponent.indexOf(")", openingBrackets);
		String keys = pathComponent.substring(openingBrackets, closingBrackets);
		if (keys.isEmpty()) {
			throw new IllegalArgumentException("Keys can not be empty");
		}

		Matcher m = keyPattern.matcher(keys);
		Map<String, String> keyMap = Maps.newHashMap();
		while (m.find()) {
			keyMap.put(m.group(1), m.group(2));
		}
		return keyMap;
	}
}
