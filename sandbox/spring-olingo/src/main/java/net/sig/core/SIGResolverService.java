package net.sig.core;

import java.util.Collection;
import java.util.Map;

import net.sig.core.impl.SIGEntityGateway;

public abstract class SIGResolverService extends SIGAbstractCacheStore {

	public SIGResolverService(SIGEntityGateway gateway2) {
		super(gateway2);
	}

	public void erase(Object arg0) {
		throw new UnsupportedOperationException();
	}

	public void eraseAll(Collection arg0) {
		throw new UnsupportedOperationException();
	}

	public void store(Object arg0, Object arg1) {
		throw new UnsupportedOperationException();
	}

	public void storeAll(Map arg0) {
		throw new UnsupportedOperationException();
	}

	public Map loadAll(Collection arg0) {
		throw new UnsupportedOperationException();
	}
}
