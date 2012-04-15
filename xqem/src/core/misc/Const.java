package core.misc;

public interface Const {

	public static final String KEY = "key";
	public static final String TYPE="type";
	public static final String COLUMNS="columns";
	public static final String CONDITION="condition";
	public static final String SPLIT_SYMBOL = ",";
	
	public static final String ROOT="request";
	public static final String ACTION_ATTRIBUTE="@action";
	public static final String RETRIEVE = "/"+ROOT+"/*["+ACTION_ATTRIBUTE+"='retrieve']";
	public static final String PERSIST = "/"+ROOT+"/*["+ACTION_ATTRIBUTE+"='persist']";
	
	public static final String DOCUMENT="document";
	public static final String XQUERY_PREFIX = "declare variable $"+DOCUMENT+" external;\r\n";
}
