package net.sandbox;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;

public class Utils {

	private static final Pattern keyPattern = Pattern.compile("[\\(|,]([\\w]+)=(['][\\w]+[']|[\\w])");
	
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
