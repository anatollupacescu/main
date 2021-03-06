package net.sig.core;

import java.util.List;

import net.sig.core.impl.SIGEntityGateway;

import com.tangosol.net.cache.CacheStore;

public abstract class SIGAbstractCacheStore implements CacheStore {

	private final SIGEntityGateway gateway;
	
	public SIGAbstractCacheStore(SIGEntityGateway gateway2) {
		this.gateway = gateway2;
	}

	protected SIGEntityGateway getGateway() {
		return gateway;
	}
	
	public abstract List<String> getKeyNames();
}
