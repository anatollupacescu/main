package my.nosql.datastore.exception;

public class DatastoreException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DatastoreException(final String msg, final Object... key) {
		super("Datastore Exception: " + String.format(msg, key));
	}
	
	public DatastoreException(final Throwable e, final String msg, final Object... key) {
		super("Datastore Exception: " + String.format(msg, key), e);
	}
}
