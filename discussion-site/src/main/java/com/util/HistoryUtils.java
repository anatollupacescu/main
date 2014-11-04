package com.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.model.Theme;

@SuppressWarnings("unchecked")
public class HistoryUtils {

	public static Map<String, String> get(HttpServletRequest req) {
		Map<String, String> history = (Map<String, String>) req.getSession().getAttribute(Const.HISTORY_KEY);
		if (history == null) history = new LinkedHashMap<String, String>(Const.HISTORY_LENGTH); 
		return history;
	}

	public static void update(Theme theme, HttpServletRequest req) {

		Map<String, String> history = get(req);

		int length = theme.getContent().length() < Const.HISTORY_TITLE_LENGTH ? theme.getContent().length() : Const.HISTORY_TITLE_LENGTH; 
		
		if (history.size() > Const.HISTORY_LENGTH - 1) {

			Entry<String, String> idValue = history.entrySet().iterator().next();
			
			history.remove(idValue.getKey());

		}

		history.put(theme.getId(), theme.getContent().substring(0, length));
		
		req.getSession().setAttribute(Const.HISTORY_KEY, history);
	}
}
