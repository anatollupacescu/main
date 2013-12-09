package net.sig.das;

import java.util.Collection;
import java.util.Map;

import net.sig.core.SIGAbstractCacheStore;
import net.sig.core.impl.GenericData;
import net.sig.core.impl.SIGEntityGateway;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class AccountDAS extends SIGAbstractCacheStore {

	public enum KEYS { accId };
	
	private Map<String, GenericData> accounts;
	
	public AccountDAS(SIGEntityGateway gateway) {
		super(gateway);
		final GenericData account1 = new GenericData();
		final String guid = "acc1";
		account1.put(KEYS.accId.toString(), guid);
		account1.put("name", "jora");
		account1.put("parent", "guid1");
		
		final GenericData account2 = new GenericData();
		final String guid2 = "acc2";
		account2.put(KEYS.accId.toString(), guid2);
		account2.put("name", "vasea");
		account2.put("parent", "guid1");
		
		final GenericData account3 = new GenericData();
		final String guid3 = "acc3";
		account3.put(KEYS.accId.toString(), guid2);
		account3.put("name", "vasea");
		account3.put("parent", "guid2");
		
		accounts = ImmutableMap.of(guid, account1, guid2, account2, guid3, account3);
	}
	
	public Object load(Object arg0) {
		final String guid = (String) ((Map)arg0).get(KEYS.accId.toString());
		return accounts.get(guid);
	}

	public Map loadAll(Collection guidMap) {
		if(guidMap == null) {
			return accounts;
		}
		Builder<Object, Object> builder = ImmutableMap.builder();
		for(Object id : guidMap) {
			String keyValue = (String)((Map)id).get(KEYS.accId.toString());
			GenericData account = accounts.get(keyValue);
			if(account != null) {
				builder.put(id, account);
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
