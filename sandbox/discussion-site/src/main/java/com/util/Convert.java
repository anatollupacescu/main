package com.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.model.Theme;
import com.model.User;

public class Convert {

	private final static DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	public final static String nowToString() {
		return dateToString(new Date());
	}
	
	public synchronized final static Date stringToDate(String source) {
		
		Date returnValue = null;
		
        try {
            returnValue = df.parse(source);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return returnValue;
	}
	
	public synchronized final static String dateToString(Date date) {
		String returnValue = null;
        try {
            returnValue = df.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnValue;
	}
	
	public static String themeToKey(Theme theme) {
		
		if(theme == null) return null;
		
		User author = theme.getAuthor();
		Date date = theme.getDate();
		
		if(author == null || author.getEmail() == null || date == null) return null;
		
		int hash = (author.getEmail() + "_" + date).hashCode();

		return new Integer(Math.abs(hash)).toString();
	}
	
	public static String[] listToStringArray(List<String> list) {
		
		String ret[] = new String[list.size()];
		
		int i = 0;
		
		for(String l : list) {
			ret[i++] = l;
		}
		
		return ret;
	}
	
	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("2");
		
		System.out.println(listToStringArray(list).length);
	}
}
