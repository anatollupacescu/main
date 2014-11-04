package my.nosql.datastore.exception;


public class EntityIsBusyException extends DatastoreException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EntityIsBusyException(final String key) {
		super("Entity with key %s is in a transaction right now", key);
	}
}
