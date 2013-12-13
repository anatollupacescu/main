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

public class PreferencesDAS extends SIGAbstractCacheStore {

	public static final List<String> entityKeys = ImmutableList.of("pguid");
	
	private final Map<String, GenericData> preferences;
	
	public PreferencesDAS(SIGEntityGateway gateway) {
		super(gateway);
		
		final GenericData pref1 = new GenericData();
		final String guid = "guid1";
		pref1.put("pguid", guid);
		pref1.put("pin_flag", "off");
		pref1.put("photo_flag", "on");
		final GenericKey pref1Key = new GenericKey(entityKeys, pref1);
		pref1.setKey(pref1Key);
		
		final GenericData pref2 = new GenericData();
		final String guid2 = "guid2";
		pref2.put("pguid", guid2);
		pref2.put("pin_flag", "off");
		pref2.put("photo_flag", "on");
		final GenericKey pref2Key = new GenericKey(entityKeys, pref2);
		pref2.setKey(pref2Key);
		
		preferences = ImmutableMap.of(guid, pref1, guid2, pref2);
	}

	public Object load(Object arg0) {
		final GenericKey requestKey = (GenericKey) arg0;
		final String keyValue = requestKey.get("pguid");
		return preferences.get(keyValue);
	}
	
	public void erase(Object arg0) {
	}

	public void store(Object arg0, Object arg1) {
	}
	
	public void eraseAll(Collection arg0) {
		throw new UnsupportedOperationException();
	}

	public void storeAll(Map arg0) {
		throw new UnsupportedOperationException();
	}

	public Map loadAll(Collection arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> getKeyNames() {
		return entityKeys;
	}
}
