package my.nosql.datastore.exception;

public class TransactionCommitedException extends DatastoreException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TransactionCommitedException(final String key) {
		super("Transaction with key: %s has already been commited.", key);
	}
}
