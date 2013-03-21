package my.nosql.datastore;


public interface Const {

	public static final String CF_NAME_ENTITY = "Entity";
	public static final String CF_NAME_WRITE = "Write";
	public static final String CF_NAME_READ = "Read";
	public static final String CF_NAME_DELETE = "Delete";
	public static final String CF_NAME_TRANSACTION = "Transaction";
	public static final String COLUMN_NAME_TYPE = "sys_entity_type";
	public static final String TRANSACTION_KEY_ALL = "all";
	public static final String TRANSACTION_KEY_COMMITED = "commited";
}
