package com.funny.basic;

import net.sf.json.JSONObject;

public class Util {
	
	public static String json(Object obj) {
		JSONObject json = JSONObject.fromObject(obj);
		String string = json.toString();
		return string.replaceAll("[,\\{\\}]", "\n");
	}
	
	public static String getUri(String uri, int depth) {
		return "/";
	}
}
