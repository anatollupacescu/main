package net.sig.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.sig.core.impl.SIGEntityGateway;

public abstract class SIGResolverService extends SIGAbstractCacheStore {

	public SIGResolverService(SIGEntityGateway gateway2) {
		super(gateway2);
	}

	public void erase(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("rawtypes")
	public void eraseAll(Collection arg0) {
		throw new UnsupportedOperationException();
	}

	public void store(Object arg0, Object arg1) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("rawtypes")
	public void storeAll(Map arg0) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("rawtypes")
	public Map loadAll(Collection arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> getKeyNames() {
		throw new UnsupportedOperationException();
	}
}
