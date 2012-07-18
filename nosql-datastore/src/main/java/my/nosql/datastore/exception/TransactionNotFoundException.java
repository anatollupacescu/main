package my.nosql.datastore.exception;


public class TransactionNotFoundException extends DatastoreException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TransactionNotFoundException(final String key) {
		super("No transaction has been started with key: %s", key);
	}
}
