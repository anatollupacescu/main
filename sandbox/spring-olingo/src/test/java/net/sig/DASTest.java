package net.sig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import net.sig.core.SIGAbstractCacheStore;
import net.sig.core.Segment;
import net.sig.core.impl.GenericData;
import net.sig.core.impl.GenericKey;
import net.sig.core.impl.SIGEntityGateway;
import net.sig.core.impl.SIGPathSegment;
import net.sig.core.impl.SIGSegmentExecutor;
import net.sig.das.AccountDAS;
import net.sig.das.AccountsSubscribersResolverDAS;
import net.sig.das.SubscriberDAS;
import net.sig.das.SubscribersAccountsResolverDAS;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class DASTest {

	final SIGEntityGateway gateway = new SIGEntityGateway();
	
	@Before
	public void setUp() {
		Map<String, SIGAbstractCacheStore> registry = ImmutableMap.<String, SIGAbstractCacheStore> of(
				"Subscribers", new SubscriberDAS(gateway), "Accounts", new AccountDAS(gateway),
				"SubscribersAccountsResolver", new SubscribersAccountsResolverDAS(gateway),
				"AccountsSubscribersResolver", new AccountsSubscribersResolverDAS(gateway));
		gateway.setRegistry(registry);
	}
	
	private GenericKey newAccountKey() {
		 return new GenericKey(AccountDAS.entityKeys);
	}
	
	private GenericKey newSubscriberKey() {
		 return new GenericKey(SubscriberDAS.entityKeys);
	}
	
	@Test
	public void test1() {
		GenericKey accountKey = newAccountKey();
		accountKey.inferValues(ImmutableMap.of("accId", "acc1"));
		//Accounts
		Segment accounts = SIGPathSegment.newSegment("Accounts", accountKey);
		SIGSegmentExecutor accountsExecutor = SIGSegmentExecutor.newExecutor(gateway, accounts);
		GenericData result = (GenericData)accountsExecutor.execute();
		assertNotNull(result);
		assertEquals("jora", result.get("name"));
	}
	
	@Test
	public void test2() {
		//Subscribers(guid1)/accounts
		Segment accounts = SIGPathSegment.newSegment("Accounts");
		
		GenericKey subscriberKey = newSubscriberKey(); 
		subscriberKey.inferValues(ImmutableMap.of("guid", "guid1"));
		Segment subscribers = SIGPathSegment.newSegment("Subscribers", subscriberKey);
		
		accounts.setPrev(subscribers);
		
		SIGSegmentExecutor accountsExecutor = SIGSegmentExecutor.newExecutor(gateway, accounts);
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
		Segment accounts = SIGPathSegment.newSegment("Accounts", acc1);
		
		GenericKey guid1 = newSubscriberKey(); 
		guid1.inferValues(ImmutableMap.of("guid", "guid1"));
		Segment subscribers = SIGPathSegment.newSegment("Subscribers", guid1);
		
		accounts.setPrev(subscribers);
		
		SIGSegmentExecutor accountsExecutor = SIGSegmentExecutor.newExecutor(gateway, accounts);
		GenericData result = (GenericData)accountsExecutor.execute();
		assertNotNull(result);
		assertEquals("guid1", result.get("parent"));
	}
	
	@Test
	public void test4() {
		//Subscribers(guid1)/accounts(acc1)/subscribers
		Segment subscribers = SIGPathSegment.newSegment("Subscribers");
		
		GenericKey acc1 = newAccountKey(); 
		acc1.inferValues(ImmutableMap.of("accId", "acc1"));
		Segment accounts = SIGPathSegment.newSegment("Accounts", acc1);
		
		GenericKey guid1 = newSubscriberKey(); 
		guid1.inferValues(ImmutableMap.of("guid", "guid1"));
		Segment subscriber = SIGPathSegment.newSegment("Subscribers", guid1);
		
		subscribers.setPrev(accounts);
		accounts.setPrev(subscriber);
		
		SIGSegmentExecutor accountsExecutor = SIGSegmentExecutor.newExecutor(gateway, subscribers);
		@SuppressWarnings("unchecked")
		Map<GenericKey, GenericData> result = (Map<GenericKey, GenericData>)accountsExecutor.execute();
		assertNotNull(result);
		assertEquals("21", ((GenericData)result.values().iterator().next()).get("age"));
	}

	@Test
	@Ignore
	public void test22() {
		//Subscribers(guid1)/accounts(acc1)/Subscribers(guid2)/accounts
		
	}
}
