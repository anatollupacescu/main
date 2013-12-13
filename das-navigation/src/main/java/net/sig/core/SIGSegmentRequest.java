package net.sig.core;

import java.util.Collection;

import net.sig.core.impl.GenericData;
import net.sig.core.impl.GenericKey;
import net.sig.core.impl.SIGEntityGateway;
import net.sig.core.impl.SIGPathSegment;

public abstract class SIGSegmentRequest {

	protected final SIGPathSegment current;
	protected final SIGEntityGateway gateway;
	
	public SIGSegmentRequest(SIGEntityGateway gateway, SIGPathSegment current) {
		super();
		this.current = current;
		this.gateway = gateway;
	}

	public abstract Object execute();
	
	protected boolean hasChild(GenericData prevResult, SIGPathSegment child) {
		SIGAbstractCacheStore resolverService = getResolverService(child);
		@SuppressWarnings("unchecked")
		Collection<GenericKey> entityIds = (Collection<GenericKey>) resolverService.load(prevResult.getKey());
		return entityIds.contains(current.getGuid());
	}

	private String getResolverName(SIGPathSegment child) {
		return child.getPrev().getServiceName() + child.getServiceName() + "Resolver";
	}
	
	protected SIGAbstractCacheStore getResolverService(SIGPathSegment child) {
		String resolerName = getResolverName(child);
		SIGAbstractCacheStore resolverService = gateway.getService(resolerName);
		if(resolverService == null) {
			throw new IllegalStateException("Could not find resolver service for " + resolerName);
		}
		return resolverService;
	}
}
