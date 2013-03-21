package my.nosql.datastore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import my.nosql.datastore.exception.DatastoreException;
import my.nosql.datastore.exception.EntityIsBusyException;
import my.nosql.datastore.exception.TransactionBeginException;
import my.nosql.datastore.exception.TransactionCommitedException;
import my.nosql.datastore.exception.TransactionNotFoundException;
import my.nosql.datastore.model.Entity;
import my.nosql.datastore.model.Query;


public class CassandraDatastoreService implements DatastoreService {
	
	private CassandraDao entityDao;
	private CassandraDao writeDao;
	private CassandraDao readDao;
	private CassandraDao deleteDao;
	private CassandraDao transactionDao;
	
	public void setCassandraDaoFactory(CassandraDaoFactory cassandraDaoFactory) {
		entityDao = cassandraDaoFactory.makeObject(Const.CF_NAME_ENTITY);
		writeDao = cassandraDaoFactory.makeObject(Const.CF_NAME_WRITE);
		readDao = cassandraDaoFactory.makeObject(Const.CF_NAME_READ);
		deleteDao = cassandraDaoFactory.makeObject(Const.CF_NAME_DELETE);
		transactionDao = cassandraDaoFactory.makeObject(Const.CF_NAME_TRANSACTION);
	}

	public void setCleanupAtStartup(String cleanupAtStartup) throws DatastoreException {
		if(Boolean.valueOf(cleanupAtStartup) == Boolean.TRUE) {
			cleanupTransactions();
		}
	}
	
	public void beginTransaction(String key) throws DatastoreException {
		String t = transactionDao.get(Const.TRANSACTION_KEY_ALL, key);
		if(t != null) throw new TransactionBeginException(key);
		transactionDao.put(Const.TRANSACTION_KEY_ALL, key, "");
	}

	public void commitTransaction(String transactionKey) throws DatastoreException {
	
		validateTransaction(transactionKey);
		
		/*commit*/
		transactionDao.put(Const.TRANSACTION_KEY_COMMITED, transactionKey, "");
		
		Map<String, String> writeMap = writeDao.get(transactionKey);

		/*copy modified data to entity*/
		Set<String> keys = writeMap.keySet();

		for(String entityKey : keys) {
			Map<String, String> entityColumnsMap = writeDao.get(compositeKey(transactionKey,entityKey));
			Set<String> columns = entityColumnsMap.keySet();
			for(String column : columns) {
				String value = writeDao.get(entityKey, column);
				entityDao.put(entityKey, column, value);
			}
		}
		
		/*deletion*/
		Map<String, String> deleteMap = deleteDao.get(transactionKey);
		keys = deleteMap.keySet();

		for(String entityKey : keys) {
			Map<String, String> res = deleteDao.get(compositeKey(transactionKey,entityKey));
			Set<String> columns = res.keySet();
			for(String c : columns) {
				entityDao.del(entityKey, c);
				deleteDao.del(compositeKey(transactionKey,entityKey), c);
			}
			deleteDao.del(transactionKey, entityKey);
		}
		
		/*delete all from Read (remove lock)*/
		Map<String, String> readMap = readDao.get(transactionKey);
		keys = readMap.keySet();
		
		for(String entityKey : keys) {
			Map<String, String> res = readDao.get(entityKey);
			Set<String> columns = res.keySet();
			for(String c : columns) {
				readDao.del(entityKey, c);
			}
			readDao.del(transactionKey, entityKey);
		}
		
		/*clean up*/
		keys = writeMap.keySet();
		
		for(String entityKey : keys) {
			Map<String, String> res = writeDao.get(compositeKey(transactionKey,entityKey));
			Set<String> columns = res.keySet();
			for(String c : columns) {
				writeDao.del(entityKey, c);
				writeDao.del(compositeKey(transactionKey,entityKey), c);
			}
			writeDao.del(transactionKey, entityKey);
		}
		
		transactionDao.del(Const.TRANSACTION_KEY_ALL, transactionKey);
		transactionDao.del(Const.TRANSACTION_KEY_COMMITED, transactionKey);
	}

	public Entity[] pull(Query[] queries) throws DatastoreException {
		
		if(queries == null || queries.length == 0) return null;
		
		List<Entity> entities = new ArrayList<Entity>();
		
		for(Query query : queries) {
			List<Entity> list = pull(query);
			entities.addAll(list);
		}
		
		Entity[] entityArray = new Entity[entities.size()];
		
		return entities.toArray(entityArray);
	}
	
	public Entity[] pull(Query[] queries, String transactionKey) throws DatastoreException {

		if (queries == null)
			return new Entity[] {};

		if (transactionKey == null)
			return new Entity[] {};

		for(Query query : queries) {
			query.setTransaction(transactionKey);
		}

		Entity[] entities = pull(queries);
	
		return entities;
	}

	public void push(Entity[] entities) throws DatastoreException {
		
		if(entities == null || entities.length == 0) return;
		
		/*check if there are entities in a transaction*/
		for(Entity entity : entities) {
			String key = entity.getKey();
			Set<String> columns = entity.getMap().keySet();
			for(String columnName : columns) {
				String value = readDao.get(key, columnName);
				if(value != null) throw new EntityIsBusyException(key);
			}
		}
		
		/*if no, we save them*/
		for(Entity entity : entities) {
			entityDao.put(entity.getKey(), entity.getMap());
			if(entity.getType() != null) {
				entityDao.put(entity.getKey(), Const.COLUMN_NAME_TYPE, entity.getType());
			}
		}
	}

	public void push(Entity[] entities, String transactionKey) throws DatastoreException {
		
		if(entities == null || transactionKey == null) return;
		
		validateTransaction(transactionKey); 
		
		/*check if Entities are not already in transaction*/
		for(Entity entity : entities) {
			Set<String> columns = entity.getMap().keySet();
			for(String column : columns) {
				String transaction = readDao.get(entity.getKey(), column);
				if(transaction != null && !transaction.equals(transactionKey)) 
					throw new EntityIsBusyException(entity.getKey());
			}
		}
		
		/*add entities to Write column family*/
		for(Entity entity : entities) {
			writeDao.put(transactionKey, entity.getKey(), "");
			Iterator<Entry<String, String>> iterator = entity.getMap().entrySet().iterator();
			while(iterator.hasNext()) {
				Entry<String, String> entry = iterator.next();
				writeDao.put(entity.getKey(), entry.getKey(), entry.getValue());
				writeDao.put(compositeKey(transactionKey,entity.getKey()), entry.getKey(), "");
			}
		}
	}

	public void delete(Entity[] entities) throws EntityIsBusyException {
		
		for (Entity entity : entities) {
			String key = entity.getKey();
			Set<String> columns = entity.getMap().keySet();
			for(String column : columns) {
				String transactionRef = readDao.get(key, column);
				if(transactionRef != null) throw new EntityIsBusyException(key);
			}
		}
		
		for (Entity entity : entities) {
			entityDao.del(entity.getKey(), entity.getMap().keySet());
		}
	}
	
	public void delete(Entity[] entities, String transactionKey) throws EntityIsBusyException {
		
		for (Entity entity : entities) {
			String key = entity.getKey();
			Set<String> columns = entity.getMap().keySet();
			for(String column : columns) {
				String transactionRef = readDao.get(key, column);
				if(transactionRef != null && !transactionRef.equals(transactionKey)) 
					throw new EntityIsBusyException(key);
			}
		}

		for (Entity entity : entities) {
			String key = entity.getKey();
			Set<String> columns = entity.getMap().keySet();
			for (String column : columns) {
				deleteDao.put(compositeKey(transactionKey,key), column, "");
			}
			deleteDao.put(transactionKey, key, "");
		}
	}
	
	public void rollbackTransaction(String transactionKey) throws DatastoreException {
		
		validateTransaction(transactionKey);
		
		Map<String, String> map = null;
		
		/*clear Read column family*/
		Map<String, String> readedEntities = readDao.get(transactionKey);
		for(String key : readedEntities.keySet()) {
			map = readDao.get(key);
			for(String column : map.keySet()) {
				readDao.del(key, column);
			}
			readDao.del(transactionKey, key);
		}
		
		/*clear Write column family*/
		Map<String, String> writeMap = writeDao.get(transactionKey);
		
		for(String key : writeMap.keySet()) {
			String compositeKey = compositeKey(transactionKey, key);
			map = writeDao.get(compositeKey);
			for(String column : map.keySet()) {
				writeDao.del(compositeKey, column);
				writeDao.del(key, column);
			}
			writeDao.del(transactionKey, key);
		}
		
		/*clear Delete column family*/
		Map<String, String> deleteMap = deleteDao.get(transactionKey);
		
		for(String key : deleteMap.keySet()) {
			String compositeKey = compositeKey(transactionKey, key);
			map = deleteDao.get(compositeKey);
			for(String column : map.keySet()) {
				deleteDao.del(compositeKey, column);
			}
			deleteDao.del(transactionKey, key);
		}
		
		transactionDao.del(Const.TRANSACTION_KEY_ALL, transactionKey);
	}

	public void cleanupTransactions() throws DatastoreException {
		
		/*finish commiting transactions*/
		Map<String, String> map = transactionDao.get(Const.TRANSACTION_KEY_ALL);
		Set<String> set = map.keySet();
		
		/*check if transaction has been interrupted in terminal phase*/
		for(String key : set) {
			String all = transactionDao.get(Const.TRANSACTION_KEY_ALL, key);
			String commited = transactionDao.get(Const.TRANSACTION_KEY_COMMITED, key);
			
			if(all == null && commited != null) {
				transactionDao.del(Const.TRANSACTION_KEY_COMMITED, key);
			}
		}
		
		map = transactionDao.get(Const.TRANSACTION_KEY_COMMITED);
		set = map.keySet();
		
		for(String key : set) {
			commitTransaction(key);
		}
		
		map = transactionDao.get(Const.TRANSACTION_KEY_ALL);
		set = map.keySet();
		
		for(String key : set) {
			rollbackTransaction(key);
		}
	}

	private Entity getEntityForQuery(Query query) throws DatastoreException, EntityIsBusyException {
		
		if(query == null) return null;
		
		if(query.getTransaction() != null) {
			return getEntityForTQuery(query);
		}
		
		Map<String, String> entityMap = new HashMap<String, String>(query.columns.length + 1);
		String key = query.getKey();
		
		for(String column : query.columns) {
			
			String value = null;
			String transaction = readDao.get(key, column);
			
			if(transaction != null) {
				
				if(transactionCommited(transaction)) {
					
					value = deleteDao.get(key, column);
					
					if (value != null) {
						value = null;
					} else {
						value = writeDao.get(key, column);
					}
				} else {
					value = entityDao.get(key, column);
				}
			} else {
				value = entityDao.get(key, column);
			}
			
			entityMap.put(column, value);
		}
		
		String type = query.type;

		if(type == null) {
			type = entityDao.get(key, Const.COLUMN_NAME_TYPE);
		}
		
		return new Entity(query.getKey(), type, entityMap);
	}
	
	private Entity getEntityForTQuery(Query query) throws DatastoreException {
		
		Map<String, String> entityMap = new HashMap<String, String>(query.columns.length + 1);
		
		String key = query.getKey();
		String queryTransaction = query.getTransaction();
		
		validateTransaction(queryTransaction);
		
		for (String column : query.columns) {

			String value = null;
			String entityTransaction = readDao.get(key, column);

			if (entityTransaction == null) { // column is free
				readDao.put(key, column, queryTransaction);
			} else if (!entityTransaction.equals(queryTransaction)) {
				throw new EntityIsBusyException(key); //column is in another transaction
			}

			value = entityDao.get(key, column);

			entityMap.put(column, value);
		}
		
		readDao.put(queryTransaction, key, "");
		
		String type = query.type;

		if(type == null) {
			type = entityDao.get(key, Const.COLUMN_NAME_TYPE);
		}
		
		return new Entity(query.getKey(), type, entityMap);
	}

	private List<Entity> pull(Query query) throws DatastoreException {
		
		List<Entity> list = new ArrayList<Entity>();
		
		if(query.getKey() != null) {
			Entity entity = getEntityForQuery(query);
			list.add(entity);
		} else {
			List<Query> queryList = search(query);
			for(Query q : queryList) {
				Entity entity = getEntityForQuery(q);
				list.add(entity);
			}
		}
		
		return list;
	}

	/**
	 * 
	 * searches for keys based on query conditions
	 * 
	 * @param query
	 * @return A list of copies of the initial query but with populated key
	 * 
	 */
	private List<Query> search(Query query) {
		String[] keys = entityDao.search(query.type, query.conditions);
		List<Query> queries = new ArrayList<Query>(keys.length);
		for(String key : keys) {
			Query q = new Query(key, query.type, query.columns);
			queries.add(q);
		}
		return queries;
	}
	
	private String compositeKey(String transactionKey, String key) {
		return transactionKey + "_" + key;
	}
	
	private boolean transactionExists(String transactionKey) {
		return null != transactionDao.get(Const.TRANSACTION_KEY_ALL, transactionKey);
	}

	private boolean transactionCommited(String transactionKey) {
		return null != transactionDao.get(Const.TRANSACTION_KEY_COMMITED, transactionKey);
	}
	
	private void validateTransaction(String key) throws DatastoreException {
		
		if (!transactionExists(key)) {
			throw new TransactionNotFoundException(key);
		}
		
		if (transactionCommited(key)) {
			throw new TransactionCommitedException(key);
		}
	}
}
