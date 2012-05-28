package net.xqwf;

public interface Const {

	public static final String _NEXT = "//_next";
	
	public static final String STRING_VALUE = _NEXT + "/stringValue";
	public static final String STRING_LIST = _NEXT + "/stringList";
	public static final String OBJECT_LIST = _NEXT + "/objectList";
	public static final String NEXT_CODE = _NEXT + "/code";
	
	public static final String REQUEST = "request";
	public static final String RESPONSE = "response";
	
	public static final String POST = "post";
	public static final String GET = "get";
	public static final String METHOD = "method";
	
	public static final String KEY = "key";
	public static final String TYPE = "type";
	public static final String COLUMNS = "columns";
	public static final String CONDITION = "condition";
	public static final String SPLIT_SYMBOL = ",";
	
	public static final String ROOT = REQUEST;
	
	public static final String ACTION = "action";
	public static final String PERSIST = "persist";
	public static final String RETRIEVE = "retrieve";
	
	public static final String RETRIEVE_QUERY = "/" + ROOT + "/*[@" + ACTION + "='"+RETRIEVE+"']";
	public static final String PERSIST_QUERY = "/" + ROOT + "/*[@" + ACTION + "='"+PERSIST+"']";
	
	public static final String DOCUMENT = "document";
	public static final String XQUERY_PREFIX = "declare variable $"+DOCUMENT+" external;\r\n";
}
