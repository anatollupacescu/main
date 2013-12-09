package net.sig.core.impl;

import java.util.Map;

import net.sig.core.SIGAbstractCacheStore;

public final class SIGEntityGateway {

	private Map<String, SIGAbstractCacheStore> registry;
	
	public void setRegistry(Map<String, SIGAbstractCacheStore> registry2) {
		this.registry = registry2;
	}

	public SIGAbstractCacheStore getService(String arg0) {
		SIGAbstractCacheStore store = registry.get(arg0);
		if(store == null) {
			throw new RuntimeException("Could not find service " + arg0);
		}
		return registry.get(arg0);
	}
}
