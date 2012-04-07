package com.util;

public class Const {
	
	public final static String KEYSPACE = "discussion";
	
	public final static String APPLICATION_NAME = "discussion";
	
	public final static String JSP_DIRECTORY="jsp/";
	public final static String JSP_SUFFIX=".jsp";
	
	public final static String THEME="theme";
	public final static String THEMES="themes";
	public final static String ERROR="error";
	public final static String USER="user";
	
	public final static String JSP_THEMES	= JSP_DIRECTORY + THEMES + JSP_SUFFIX;
	public final static String JSP_THEME	= JSP_DIRECTORY + THEME + JSP_SUFFIX;
	public final static String JSP_ERROR	= JSP_DIRECTORY + ERROR + JSP_SUFFIX;
	public final static String JSP_USER		= JSP_DIRECTORY + USER + JSP_SUFFIX;
	
	public final static String THEME_KEY = "view";
	
	public final static String TABLE_USER="user";
	public final static String TABLE_THEME="theme";
	public final static String TABLE_INDEX_SUFFIX="_index";
	public final static String THEME_INDEX=TABLE_THEME+TABLE_INDEX_SUFFIX;
	
	public final static int HISTORY_LENGTH = 4;
	public final static int HISTORY_TITLE_LENGTH = 8;
	public final static String HISTORY_KEY = "HISTORY_KEY";
	public final static String HISTORY_TITLE = "HISTORY_TITLE";
	public final static String HISTORY_URL = "HISTORY_URL";

	public final static String history="history";
	
	public static final String deleteParam = "delete";
	
	public static final String DEFAULT_PARENT_ID = "0";
	
	public static final int THEMES_PER_PAGE = 5;
	public static final String THEME_DATE_INDEX = "theme_date_index";
	public static final String TITLE_KEY = "title";
}
