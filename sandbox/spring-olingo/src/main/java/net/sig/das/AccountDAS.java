package net.sig.das;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.sig.core.SIGAbstractCacheStore;
import net.sig.core.impl.GenericData;
import net.sig.core.impl.GenericKey;
import net.sig.core.impl.SIGEntityGateway;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class AccountDAS extends SIGAbstractCacheStore {

	public static final List<String> entityKeys = ImmutableList.of("accId");
	
	private Map<String, GenericData> accounts;
	
	public AccountDAS(SIGEntityGateway gateway) {
		super(gateway);
		final GenericData account1 = new GenericData();
		final GenericKey account1Key = new GenericKey(entityKeys);
		account1.setKey(account1Key);
		final String guid = "acc1";
		account1.put("accId", guid);
		account1.put("name", "jora");
		account1.put("parent", "guid1");
		account1.inferKeyValues();
		
		final GenericData account2 = new GenericData();
		final GenericKey account2Key = new GenericKey(entityKeys);
		account2.setKey(account2Key);
		final String guid2 = "acc2";
		account2.put("accId", guid2);
		account2.put("name", "vasea");
		account2.put("parent", "guid1");
		account2.inferKeyValues();
		
		final GenericData account3 = new GenericData();
		final GenericKey account3Key = new GenericKey(entityKeys);
		account3.setKey(account3Key);
		final String guid3 = "acc3";
		account3.put("accId", guid3);
		account3.put("name", "vasea");
		account3.put("parent", "guid2");
		account3.inferKeyValues();
		
		accounts = ImmutableMap.of(guid, account1, guid2, account2, guid3, account3);
	}
	
	public Object load(Object arg0) {
		final GenericKey queryKey = (GenericKey) arg0;
		final String guid = queryKey.get("accId");
		return accounts.get(guid);
	}

	public Map loadAll(Collection guidMap) {
		if(guidMap == null) {
			return accounts;
		}
		Builder<GenericKey, GenericData> builder = ImmutableMap.builder();
		for(Object id : guidMap) {
			GenericKey accountKey = (GenericKey)id;
			GenericData account = (GenericData)load(accountKey);
			if(account != null) {
				builder.put(accountKey, account);
			}
		}
		return builder.build();
	}
	
	public void erase(Object arg0) {
		// TODO Auto-generated method stub

	}

	public void eraseAll(Collection arg0) {
		// TODO Auto-generated method stub

	}

	public void store(Object arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	public void storeAll(Map arg0) {
		// TODO Auto-generated method stub

	}

}
