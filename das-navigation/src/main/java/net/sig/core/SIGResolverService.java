package net.sig.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.sig.core.impl.SIGEntityGateway;

public abstract class SIGResolverService extends SIGAbstractCacheStore {

	protected final Map<String, String> keyMapping;

	public SIGResolverService(SIGEntityGateway gateway2) {
		super(gateway2);
		this.keyMapping = null;
	}
	
	public SIGResolverService(SIGEntityGateway gateway2, Map<String, String> keyMapping) {
		super(gateway2);
		this.keyMapping = keyMapping;
	}

	public Map<String, String> getKeyMappingMap() {
		return keyMapping;
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
