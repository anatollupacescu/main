package core;

public interface Const {

	public static final String _NEXT = "//_next";
	
	public static final String _NEXT_PAGE = _NEXT + "/page";
	public static final String _NEXT_PARAM = _NEXT + "/param";
	public static final String _NEXT_NODE = _NEXT + "/node";
	
	public static final String XPATH = "xpath";
	
	public static final String CONFIG_FILE_PATH = "c:\\Users\\Anatol\\workspace\\web-app\\src\\config.properties";
	
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
	public static final String KEEP = "keep";
	
	public static final String RETRIEVE_QUERY = "/" + ROOT + "/*[@" + ACTION + "='"+RETRIEVE+"']";
	public static final String PERSIST_QUERY = "/" + ROOT + "/*[@" + ACTION + "='"+PERSIST+"']";
	
	public static final String DOCUMENT = "document";
	public static final String XQUERY_PREFIX = "declare variable $"+DOCUMENT+" external;\r\n";
	
	public static final String PAGE_SYSTEM_ERROR =  "system/error";
}
