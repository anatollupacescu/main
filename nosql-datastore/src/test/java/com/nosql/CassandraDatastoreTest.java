package com.nosql;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import my.nosql.datastore.CassandraDaoFactory;
import my.nosql.datastore.CassandraDatastoreService;
import my.nosql.datastore.Const;
import my.nosql.datastore.exception.DatastoreException;
import my.nosql.datastore.model.Entity;
import my.nosql.datastore.model.Query;
import my.nosql.datastore.model.QueryCondition;
import my.nosql.datastore.model.QueryOperator;

import org.junit.Assert;
import org.junit.Test;


public class CassandraDatastoreTest {
	
	private CassandraDatastoreService datastoreService = new CassandraDatastoreService();
	
	{
		CassandraDaoFactory factory = new CassandraDaoFactory();
		datastoreService.setCassandraDaoFactory(factory);
	}
	
	@Test
	public void bredTest() throws DatastoreException {
		Query query = new Query("jora@yahoo", null, new String[] {"age"});
		Entity[] entityArray = datastoreService.pull( new Query[] { query });
		System.out.println(Arrays.asList(entityArray));
	}
//	@Test
	public void transaction() throws DatastoreException {
		String key = "124";
		datastoreService.beginTransaction(key);
		try {
			datastoreService.beginTransaction(key);
		} catch (Exception e) {
			Assert.assertEquals(e.getMessage(), "Transaction Exception: Could not begin transaction with key " + key);
		}
		
		datastoreService.rollbackTransaction(key);
		
		datastoreService.beginTransaction(key);
		datastoreService.commitTransaction(key);
		
		try {
			datastoreService.rollbackTransaction(key);
		} catch (Exception e) {
			Assert.assertEquals(e.getMessage(), "Transaction Exception: No transaction has been started with key: " + key);
		}
		
		datastoreService.beginTransaction(key);
		datastoreService.cleanupTransactions();
		
		try {
			datastoreService.rollbackTransaction(key);
		} catch (Exception e) {
			Assert.assertEquals(e.getMessage(), "Transaction Exception: No transaction has been started with key: " + key);
		}
	}
	
//	@Test
	public void transactionalPushSimplePull() throws DatastoreException {
		Map<String,String> userMap = new HashMap<String, String>(1);
		userMap.put("name", "Jora");
		userMap.put("account", "511");
		userMap.put("age", "27");
		
		Entity user = new Entity("jora@yahoo", "user", userMap);
		datastoreService.push(new Entity[] { user });
		
		Query query = new Query("jora@yahoo", "user", new String[] {"age"});
		Entity[] res = datastoreService.pull(new Query[] { query });
		
		Assert.assertNotNull(res);
		Assert.assertEquals(1, res.length);
		Assert.assertEquals("27", res[0].getMap().get("age").toString());
		
		String transactionKey = "2";
		datastoreService.beginTransaction(transactionKey );
		userMap = new HashMap<String, String>(1);
		userMap.put("age", "29");
		user = new Entity("jora@yahoo", "user", userMap);
		datastoreService.push(new Entity[] { user }, transactionKey);
		
		res = datastoreService.pull(new Query[] { query });
		
		Assert.assertNotNull(res);
		Assert.assertEquals(1, res.length);
		Assert.assertEquals("27", res[0].getMap().get("age").toString());
		
		datastoreService.commitTransaction(transactionKey);
		
		res = datastoreService.pull(new Query[] { query });
		
		Assert.assertNotNull(res);
		Assert.assertEquals(1, res.length);
		Assert.assertEquals("29", res[0].getMap().get("age").toString());
	}
	
//	@Test
	public void simplePushPull() throws DatastoreException {
		Map<String,String> userMap = new HashMap<String, String>(2);
		userMap.put("name", "Anatol");
		userMap.put("account", "111");
		userMap.put("age", "26");
		userMap.put(Const.COLUMN_NAME_TYPE, "user");
		
		Entity user = new Entity("tolea@gmail", "user", userMap);
		datastoreService.push(new Entity[] { user });
		
		Query query = new Query("tolea@gmail", "user", new String[] {"account"});
		Entity[] res = datastoreService.pull(new Query[] { query });
		
		Assert.assertNotNull(res);
		Assert.assertEquals(1, res.length);
		Assert.assertEquals("111", res[0].getMap().get("account").toString());
		
		datastoreService.delete(new Entity[] {user});
		query = new Query("tolea@gmail", "user", new String[] {"account"});
		res = datastoreService.pull(new Query[] { query });
		
		Assert.assertNotNull(res);
		Assert.assertEquals(1, res.length);
		Assert.assertNull(res[0].getMap().get("account"));
	}
	
//	@Test
	public void transactionalPushPull() throws DatastoreException {
		
		Query query = new Query("tolea@gmail", "user", new String[] {"account"});
		query.setTransaction("1");
		
		try {
			datastoreService.pull(new Query[] { query });
		} catch (Exception e) {
			Assert.assertEquals("Datastore Exception: No transaction has been started with key: 1", e.getMessage());
		}
		
		datastoreService.beginTransaction("1");
		Entity[] entity = datastoreService.pull(new Query[] { query });
		
		Assert.assertEquals(1, entity.length);
		
		datastoreService.commitTransaction("1");
	}
	
//	@Test
	public void myTest() throws DatastoreException {
		
		Map<String,String> userMap = new HashMap<String, String>(2);
		userMap.put("name", "Anatol");
		userMap.put("account", "111");
		userMap.put("age", "26");
		userMap.put(Const.COLUMN_NAME_TYPE, "user");
		
		Entity user = new Entity("tolea@gmail", "user", userMap);
		datastoreService.push(new Entity[] { user });
		
		Query query = new Query("tolea@gmail", "user", new String[] {"account"});
		Entity[] res = datastoreService.pull(new Query[] { query });
		
		Assert.assertNotNull(res);
		Assert.assertEquals(1, res.length);
		Assert.assertEquals("111", res[0].getMap().get("account").toString());		
		
		String key = "1337536124692";
		
		datastoreService.beginTransaction(key);
		
		datastoreService.pull(new Query[] { query }, key);		
		
		boolean error = false;
		try {
			datastoreService.pull(new Query[] { query }, "vasea");
		} catch (Exception e) {
			error = true;
		}
		
		Assert.assertEquals(error, true);
		
		Map<String,String> localMap = new HashMap<String, String>(userMap);
		localMap.put("account", "333");
		localMap.put(Const.COLUMN_NAME_TYPE, "user");
		
		user = new Entity("tolea@gmail", "user", localMap);
		
		error = false;
		try {
			datastoreService.push(new Entity[] { user });
		} catch (Exception e) {
			error = true;
		}
		
		Assert.assertEquals(error, true);
		
		error = false;
		try {
			datastoreService.delete(new Entity[] { user });
		} catch (Exception e) {
			error = true;
		}
		
		Assert.assertEquals(error, true);
		
		Map<String,String> deleteUserMap = new HashMap<String, String>(2);
		deleteUserMap.put("name", "Anatol");
		Entity deleteUser = new Entity("tolea@gmail", "user", deleteUserMap);
		datastoreService.delete(new Entity[] { deleteUser }, key);
		
		datastoreService.push(new Entity[] { user }, key);
		
		res = datastoreService.pull(new Query[] { query });
		
		Assert.assertNotNull(res);
		Assert.assertEquals(1, res.length);
		Assert.assertEquals("111", res[0].getMap().get("account").toString());		
		
		QueryCondition qq = new QueryCondition("name", QueryOperator.EQ, "Anatol");
		
		QueryCondition[] queryCondition = new QueryCondition[] { qq };
		Query searchQuery = new Query("user", new String[] {"age"}, queryCondition);
		res = datastoreService.pull(new Query[] { searchQuery }, key);
		
		Assert.assertNotNull(res);
		Assert.assertEquals(1, res.length);
		Assert.assertEquals("26", res[0].getMap().get("age").toString());	
		
		datastoreService.commitTransaction(key);
		res = datastoreService.pull(new Query[] { query });
		
		Assert.assertNotNull(res);
		Assert.assertEquals(1, res.length);
		Assert.assertEquals("333", res[0].getMap().get("account").toString());
	}
}
