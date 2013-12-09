package net.sig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import net.sig.core.SIGAbstractCacheStore;
import net.sig.core.Segment;
import net.sig.core.impl.SIGEntityGateway;
import net.sig.core.impl.SIGPathSegment;
import net.sig.core.impl.SIGSegmentExecutor;
import net.sig.das.AccountDAS;
import net.sig.das.AccountsSubscribersResolverDAS;
import net.sig.das.SubscriberDAS;
import net.sig.das.SubscribersAccountsResolverDAS;

import org.junit.Before;
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
	
	@Test
	public void test1() {
		Map guid = ImmutableMap.of(AccountDAS.KEYS.accId, "acc1");
		//Accounts
		Segment accounts = SIGPathSegment.newSegment("Accounts", guid );
		SIGSegmentExecutor accountsExecutor = SIGSegmentExecutor.newExecutor(gateway, accounts);
		Object result = accountsExecutor.execute();
		assertNotNull(result);
		assertEquals("jora", ((Map)result).get("name"));
	}
	
	@Test
	public void test2() {
		//Subscribers(guid1)/accounts
		Segment accounts = SIGPathSegment.newSegment("Accounts");
		Map guid1 = ImmutableMap.of(SubscriberDAS.KEYS.guid, "guid1");
		Segment subscribers = SIGPathSegment.newSegment("Subscribers", guid1);
		
		accounts.setPrev(subscribers);
		
		SIGSegmentExecutor accountsExecutor = SIGSegmentExecutor.newExecutor(gateway, accounts);
		Map result = (Map)accountsExecutor.execute();
		assertNotNull(result);
		assertEquals(2, result.values().size());
	}

	@Test
	public void test3() {
		//Subscribers(guid1)/accounts(acc1)
		Map acc1 = ImmutableMap.of(AccountDAS.KEYS.accId, "acc1");
		Segment accounts = SIGPathSegment.newSegment("Accounts", acc1);
		Map guid1 = ImmutableMap.of(SubscriberDAS.KEYS.guid, "guid1");
		Segment subscribers = SIGPathSegment.newSegment("Subscribers", guid1);
		
		accounts.setPrev(subscribers);
		
		SIGSegmentExecutor accountsExecutor = SIGSegmentExecutor.newExecutor(gateway, accounts);
		Map result = (Map)accountsExecutor.execute();
		assertNotNull(result);
		assertEquals("guid1", result.get("parent"));
	}
	
	@Test
	public void test4() {
		//Subscribers(guid1)/accounts(acc1)/subscribers
		Segment subscribers = SIGPathSegment.newSegment("Subscribers");
		
		Map acc1 = ImmutableMap.of(AccountDAS.KEYS.accId, "acc1");
		Segment accounts = SIGPathSegment.newSegment("Accounts", acc1);
		
		Map guid1 = ImmutableMap.of(SubscriberDAS.KEYS.guid, "guid1");
		Segment subscriber = SIGPathSegment.newSegment("Subscribers", guid1);
		
		subscribers.setPrev(accounts);
		accounts.setPrev(subscriber);
		
		SIGSegmentExecutor accountsExecutor = SIGSegmentExecutor.newExecutor(gateway, subscribers);
		Map result = (Map)accountsExecutor.execute();
		assertNotNull(result);
		assertEquals("21", ((Map)result.values().iterator().next()).get("age"));
	}
	
	@Test
	public void test22() {
		//Subscribers(guid1)/accounts(acc1)/Subscribers(guid2)/accounts
		
		Segment accounts = SIGPathSegment.newSegment("accounts");
		Segment subscribers_guid2 = SIGPathSegment.newSegment("subscribers", ImmutableMap.of("guid", "guid2"));
		Segment accounts_acc1 = SIGPathSegment.newSegment("accounts", ImmutableMap.of("accId", "acc1"));
		Segment subscribers_guid1 = SIGPathSegment.newSegment("subscribers", ImmutableMap.of("guid", "guid1"));
		
		accounts.setPrev(subscribers_guid2);
		subscribers_guid2.setPrev(accounts_acc1);
		accounts_acc1.setPrev(subscribers_guid1);
		
		SIGSegmentExecutor accountsExecutor = SIGSegmentExecutor.newExecutor(gateway, accounts);
		Object result = accountsExecutor.execute();
	}
}
