package my.nosql.datastore.exception;

public class TransactionBeginException extends DatastoreException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TransactionBeginException(final Exception e, final String key) {
		super(e, "Could not begin transaction with key %s", key);
	}
	
	public TransactionBeginException(final String key) {
		super("Could not begin transaction with key %s", key);
	}
}
