package my.nosql.datastore;

import my.nosql.datastore.exception.DatastoreException;
import my.nosql.datastore.model.Entity;
import my.nosql.datastore.model.Query;

public interface DatastoreService {

	public void beginTransaction(String key) throws DatastoreException;
	public void commitTransaction(String key) throws DatastoreException;
	public void rollbackTransaction(String key) throws DatastoreException;
	public void cleanupTransactions() throws DatastoreException;
	
	public Entity[] pull(Query[] queries) throws DatastoreException;
	public Entity[] pull(Query[] queries, String transactionKey) throws DatastoreException;
	
	public void push(Entity[] entities) throws DatastoreException;
	public void push(Entity[] entities, String transactionKey) throws DatastoreException;
	
	public void delete(Entity[] entities) throws DatastoreException;
	public void delete(Entity[] entities, String transactionKey) throws DatastoreException;
}
