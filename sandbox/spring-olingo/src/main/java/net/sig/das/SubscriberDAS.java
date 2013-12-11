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

public class SubscriberDAS extends SIGAbstractCacheStore {

	public static final List<String> entityKeys = ImmutableList.of("guid");
	
	private final Map<String, GenericData> subscribers;
	
	public SubscriberDAS(SIGEntityGateway gateway) {
		super(gateway);
		final GenericData subscriber1 = new GenericData();
		final GenericKey subscriber1Key = new GenericKey(entityKeys);
		subscriber1.setKey(subscriber1Key);
		final String guid = "guid1";
		subscriber1.put("guid", guid);
		subscriber1.put("age", "21");
		subscriber1.inferKeyValues();
		
		final GenericData subscriber2 = new GenericData();
		final GenericKey subscriber2Key = new GenericKey(entityKeys);
		subscriber2.setKey(subscriber2Key);
		final String guid2 = "guid2";
		subscriber2.put("guid", guid2);
		subscriber2.put("age", "58");
		subscriber2.inferKeyValues();
		
		subscribers = ImmutableMap.of(guid, subscriber1, guid2, subscriber2);
	}
	
	public Object load(Object arg0) {
		final GenericKey requestKey = (GenericKey) arg0;
		final String keyValue = requestKey.get("guid");
		return subscribers.get(keyValue);
	}
	
	public Map loadAll(Collection ids) {
		if(ids == null) {
			return subscribers;
		}
		Builder<GenericKey, GenericData> builder = ImmutableMap.builder();
		for(Object id : ids) {
			GenericKey subscriberKey = (GenericKey)id;
			GenericData subcriber = subscribers.get(subscriberKey.get("guid"));
			if(subcriber != null) {
				builder.put(subscriberKey, subcriber);
			}
		}
		return builder.build();
	}
	
	public void erase(Object arg0) {
	}

	public void eraseAll(Collection arg0) {
	}

	public void store(Object arg0, Object arg1) {
	}

	public void storeAll(Map arg0) {
	}

	@Override
	public List<String> getKeyNames() {
		return entityKeys;
	}

}
