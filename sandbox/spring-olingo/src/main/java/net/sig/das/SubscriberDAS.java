package net.sig.das;

import java.util.Collection;
import java.util.Map;

import net.sig.core.SIGAbstractCacheStore;
import net.sig.core.impl.GenericData;
import net.sig.core.impl.SIGEntityGateway;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class SubscriberDAS extends SIGAbstractCacheStore {

	public enum KEYS { guid };
	
	private final Map<String, GenericData> subscribers;
	
	public SubscriberDAS(SIGEntityGateway gateway) {
		super(gateway);
		final GenericData subscriber1 = new GenericData();
		final String guid = "guid1";
		subscriber1.put(KEYS.guid.toString(), guid);
		subscriber1.put("age", "21");
		
		final GenericData subscriber2 = new GenericData();
		final String guid2 = "guid2";
		subscriber2.put(KEYS.guid.toString(), guid2);
		subscriber2.put("age", "58");
		
		subscribers = ImmutableMap.of(guid, subscriber1, guid2, subscriber2);
	}
	
	public Object load(Object arg0) {
		final String guid = (String) ((Map)arg0).get(KEYS.guid.toString());
		return subscribers.get(guid);
	}
	
	public Map loadAll(Collection ids) {
		Builder<String, GenericData> builder = ImmutableMap.builder();
		for(Map id : (Collection<Map>)ids) {
			String keyValue = (String)id.get(KEYS.guid.toString());
			GenericData subcriber = subscribers.get(keyValue);
			if(subcriber != null) {
				builder.put(keyValue, subcriber);
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

}
