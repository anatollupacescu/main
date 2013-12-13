package net.sig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;

import net.sig.core.SIGAbstractCacheStore;
import net.sig.core.impl.GenericData;
import net.sig.core.impl.GenericKey;
import net.sig.core.impl.GenericOneToOneResolverDAS;
import net.sig.core.impl.SIGEntityGateway;
import net.sig.core.impl.SIGPathSegment;
import net.sig.core.impl.SIGCreateRequest;
import net.sig.core.impl.SIGDeleteRequest;
import net.sig.core.impl.SIGRetrieveRequest;
import net.sig.das.AccountDAS;
import net.sig.das.AccountsSubscribersResolverDAS;
import net.sig.das.PreferencesDAS;
import net.sig.das.SubscriberDAS;
import net.sig.das.SubscribersAccountsResolverDAS;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class DASTest {

	final SIGEntityGateway gateway = new SIGEntityGateway();
	
	@Before
	public void setUp() {
		final Builder<String, SIGAbstractCacheStore> registryBuilder = ImmutableMap.builder();
		registryBuilder.put("Subscribers", new SubscriberDAS(gateway));
		registryBuilder.put("SubscribersAccountsResolver", new SubscribersAccountsResolverDAS(gateway));
		registryBuilder.put("Accounts", new AccountDAS(gateway));
		registryBuilder.put("AccountsSubscribersResolver", new AccountsSubscribersResolverDAS(gateway));
		/* preferences */
		PreferencesDAS preferencesDAS = new PreferencesDAS(gateway);
		registryBuilder.put("Preferences", preferencesDAS);
		registryBuilder.put("SubscribersPreferencesResolver", new GenericOneToOneResolverDAS(gateway, preferencesDAS, ImmutableMap.of("guid", "pguid")));
		/**/
		gateway.setRegistry(registryBuilder.build());
	}
	
	private GenericKey newAccountKey() {
		 return new GenericKey(AccountDAS.entityKeys);
	}
	
	private GenericKey newSubscriberKey() {
		 return new GenericKey(SubscriberDAS.entityKeys);
	}
	
	private GenericKey newPreferenceKey() {
		 return new GenericKey(PreferencesDAS.entityKeys);
	}
	
	@Test
	public void test1() {
		//Accounts(acc1)
		GenericKey accountKey = newAccountKey();
		accountKey.inferValues(ImmutableMap.of("accId", "acc1"));
		//Accounts
		SIGPathSegment accounts = SIGPathSegment.newSegment("Accounts", accountKey);
		SIGRetrieveRequest accountsExecutor = SIGRetrieveRequest.newExecutor(gateway, accounts);
		GenericData result = (GenericData)accountsExecutor.execute();
		assertNotNull(result);
		assertEquals("jora", result.get("name"));
	}
	
	@Test
	public void test2() {
		//Subscribers(guid1)/accounts
		SIGPathSegment accounts = SIGPathSegment.newSegment("Accounts");
		
		GenericKey subscriberKey = newSubscriberKey(); 
		subscriberKey.inferValues(ImmutableMap.of("guid", "guid1"));
		SIGPathSegment subscribers = SIGPathSegment.newSegment("Subscribers", subscriberKey);
		
		accounts.setPrev(subscribers);
		
		SIGRetrieveRequest accountsExecutor = SIGRetrieveRequest.newExecutor(gateway, accounts);
		@SuppressWarnings("unchecked")
		Map<GenericKey, GenericData> result = (Map<GenericKey, GenericData>)accountsExecutor.execute();
		assertNotNull(result);
		assertEquals(2, result.values().size());
	}

	@Test
	public void test3() {
		//Subscribers(guid1)/accounts(acc1)
		GenericKey acc1 = newAccountKey(); 
		acc1.inferValues(ImmutableMap.of("accId", "acc1"));
		SIGPathSegment accounts = SIGPathSegment.newSegment("Accounts", acc1);
		
		GenericKey guid1 = newSubscriberKey(); 
		guid1.inferValues(ImmutableMap.of("guid", "guid1"));
		SIGPathSegment subscribers = SIGPathSegment.newSegment("Subscribers", guid1);
		
		accounts.setPrev(subscribers);
		
		SIGRetrieveRequest accountsExecutor = SIGRetrieveRequest.newExecutor(gateway, accounts);
		GenericData result = (GenericData)accountsExecutor.execute();
		assertNotNull(result);
		assertEquals("guid1", result.get("parent"));
	}
	
	@Test
	public void test4() {
		//Subscribers(guid1)/accounts(acc1)/subscribers
		SIGPathSegment subscribers = SIGPathSegment.newSegment("Subscribers");
		
		GenericKey acc1 = newAccountKey(); 
		acc1.inferValues(ImmutableMap.of("accId", "acc1"));
		SIGPathSegment accounts = SIGPathSegment.newSegment("Accounts", acc1);
		
		GenericKey guid1 = newSubscriberKey(); 
		guid1.inferValues(ImmutableMap.of("guid", "guid1"));
		SIGPathSegment subscriber = SIGPathSegment.newSegment("Subscribers", guid1);
		
		subscribers.setPrev(accounts);
		accounts.setPrev(subscriber);
		
		SIGRetrieveRequest accountsExecutor = SIGRetrieveRequest.newExecutor(gateway, subscribers);
		@SuppressWarnings("unchecked")
		Map<GenericKey, GenericData> result = (Map<GenericKey, GenericData>)accountsExecutor.execute();
		assertNotNull(result);
		assertEquals("21", ((GenericData)result.values().iterator().next()).get("age"));
	}

	@Test
	public void test5() {
		//Subscribers(guid1)/preferences
		final String guid1value = "guid1";
		GenericKey guid1 = newSubscriberKey(); 
		guid1.inferValues(ImmutableMap.of("guid", guid1value));
		SIGPathSegment subscribers = SIGPathSegment.newSegment("Subscribers", guid1);
		
		GenericKey pref = newPreferenceKey(); 
		pref.inferValues(ImmutableMap.of(pref.getKeyNames().iterator().next(), guid1value));
		SIGPathSegment preferences = SIGPathSegment.newSegment("Preferences", pref);
		
		preferences.setPrev(subscribers);
		SIGRetrieveRequest ex = SIGRetrieveRequest.newExecutor(gateway, preferences);
		GenericData obj = (GenericData)ex.execute();
		assertNotNull(obj);
		assertEquals("off", obj.get("pin_flag"));
	}
	
	@Test
	public void test6() {
		//Subscribers(guid1)/preferences
		final String guid1value = "guid1";
		GenericKey guid1 = newSubscriberKey(); 
		guid1.inferValues(ImmutableMap.of("guid", guid1value));
		SIGPathSegment subscribers = SIGPathSegment.newSegment("Subscribers", guid1);
		
		SIGPathSegment preferences = SIGPathSegment.newSegment("Preferences");
		
		preferences.setPrev(subscribers);
		SIGRetrieveRequest ex = SIGRetrieveRequest.newExecutor(gateway, preferences);
		@SuppressWarnings("unchecked")
		Map<GenericKey, GenericData> objMap = (Map<GenericKey, GenericData>)ex.execute();
		assertNotNull(objMap);
		GenericData obj = (GenericData)objMap.values().iterator().next();
		
		assertEquals("off", obj.get("pin_flag"));
	}
	
	@Test
	public void test7() {
		//Subscribers(guid1)/accounts(acc1)/subscribers(guid1)/preferences
		
		SIGPathSegment preferences = SIGPathSegment.newSegment("Preferences");
		
		GenericKey guid1 = newSubscriberKey(); 
		guid1.inferValues(ImmutableMap.of("guid", "guid1"));
		SIGPathSegment subscriber2 = SIGPathSegment.newSegment("Subscribers", guid1);
		
		GenericKey acc1 = newAccountKey(); 
		acc1.inferValues(ImmutableMap.of("accId", "acc1"));
		SIGPathSegment accounts = SIGPathSegment.newSegment("Accounts", acc1);
		
		SIGPathSegment subscriber1 = SIGPathSegment.newSegment("Subscribers", guid1);
		preferences.setPrev(subscriber2);
		subscriber2.setPrev(accounts);
		accounts.setPrev(subscriber1);
		
		SIGRetrieveRequest accountsExecutor = SIGRetrieveRequest.newExecutor(gateway, preferences);
		@SuppressWarnings("unchecked")
		Map<GenericKey, GenericData> result = (Map<GenericKey, GenericData>)accountsExecutor.execute();
		assertNotNull(result);
		GenericData obj = (GenericData)result.values().iterator().next();
		assertEquals("off", obj.get("pin_flag"));
	}
	
	@Test
	public void test8() {
		//DELETE /Accounts(acc1)
		GenericKey accountKey = newAccountKey();
		accountKey.inferValues(ImmutableMap.of("accId", "acc1"));
		//Accounts
		SIGPathSegment accounts = SIGPathSegment.newSegment("Accounts", accountKey);
		
		//retrieve
		SIGRetrieveRequest accountsExecutor = SIGRetrieveRequest.newExecutor(gateway, accounts);
		Object result = accountsExecutor.execute();
		assertNotNull(result);
		
		//delete
		SIGDeleteRequest accountsDeleter = new SIGDeleteRequest(gateway, accounts);
		accountsDeleter.execute();
		//retrieve
		
		accountsExecutor = SIGRetrieveRequest.newExecutor(gateway, accounts);
		result = accountsExecutor.execute();
		assertNull(result);
	}
	
	@Test
	public void test9() {
		//CREATE /Accounts(acc1)
		GenericKey accountKey = newAccountKey();
		accountKey.inferValues(ImmutableMap.of("accId", "acc13"));
		
		//Accounts
		SIGPathSegment accounts = SIGPathSegment.newSegment("Accounts", accountKey);
		GenericData body = new GenericData();
		body.put("accId", "acc13");
		body.put("name", "inga");
		body.put("parent", "guid3");
		accounts.setBody(body);
		
		SIGRetrieveRequest accountsExecutor = SIGRetrieveRequest.newExecutor(gateway, accounts);
		Object result = accountsExecutor.execute();
		assertNull(result);
		
		SIGCreateRequest creator = new SIGCreateRequest(gateway, accounts);
		creator.execute();
		
		accountsExecutor = SIGRetrieveRequest.newExecutor(gateway, accounts);
		result = accountsExecutor.execute();
		assertNotNull(result);
		assertEquals("inga", ((GenericData)result).get("name"));
	}
}
