package net.xqwf;

public interface Const {

	public final static String ERROR = "error";
	public final static String NAME = "name";
	
	public static final String NEXT = "//_next";
	
	public static final String STRING_VALUE = NEXT + "/stringValue";
	public static final String STRING_LIST = NEXT + "/stringList";
	public static final String OBJECT_LIST = NEXT + "/objectList";
	public static final String NEXT_CODE = NEXT + "/code";
	
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

	public static final String TRANSACTION_BEGIN = "//transaction[@" + ACTION + "='begin']";
	public static final String TRANSACTION_COMMIT = "//transaction[@" + ACTION + "='commit']";
	public static final String TRANSACTION_ROLLBACK = "//transaction[@" + ACTION + "='rollback']";;;
}
