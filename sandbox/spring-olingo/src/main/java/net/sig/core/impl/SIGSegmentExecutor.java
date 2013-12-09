package net.sig.core.impl;

import java.util.Collection;
import java.util.Map;

import net.sig.core.SIGAbstractCacheStore;
import net.sig.core.Segment;

public class SIGSegmentExecutor {

	private final Segment current;
	private final SIGEntityGateway gateway;
	
	private SIGSegmentExecutor(SIGEntityGateway gateway, Segment segment) {
		this.gateway = gateway;
		this.current = segment;
	}
	
	public Object execute() {
		if(current.hasPrev()) {
			/* will be executing previous segment */
			final SIGSegmentExecutor previousSegmentExecutor = SIGSegmentExecutor.newExecutor(gateway, current.getPrev());
			GenericData prevSegmentResult = (GenericData)previousSegmentExecutor.execute();
			if(current.hasGuid()) {
    			/* will check if current segment is a valid child of the previous */
    			if(hasChild(prevSegmentResult, current)) {
    				final SIGAbstractCacheStore currentSegmentService = gateway.getService(current.getServiceName());
    				return (Map)currentSegmentService.load(current.getGuid());
    			} else {
    				throw new IllegalStateException(String.format("%s is not a valid child of %s", current, current.getPrev()));
    			}
			} else {
				return getChilds(current);
			}
		} else {
			final SIGAbstractCacheStore currentSegmentService = gateway.getService(current.getServiceName());
			if(current.hasGuid()) {
				return currentSegmentService.load(current.getGuid());
			} else {
				return currentSegmentService.loadAll(null);
			}
		}
	}
	
	public Map getChilds(Segment child) {
		String resolverName = getResolverName(child);
		SIGAbstractCacheStore resolverService = gateway.getService(resolverName);
		SIGAbstractCacheStore targetDAS = gateway.getService(child.getServiceName());
		Collection entityIds = (Collection) resolverService.load(child.getPrev().getGuid());
		return targetDAS.loadAll(entityIds);
	}
	
	public boolean hasChild(GenericData prevResult, Segment child) {
		String resolverName = getResolverName(child);
		SIGAbstractCacheStore resolverService = gateway.getService(resolverName);
		SIGAbstractCacheStore targetDAS = gateway.getService(child.getServiceName());
		Collection<Map<String, String>> entityIds = (Collection<Map<String, String>>) resolverService.load(prevResult);
		return containsChildId(entityIds, current.getGuid());
	}

	private boolean containsChildId(Collection<Map<String, String>> entityIds, Map<String, String> childGuidMap) {
		for(Map guidMap : entityIds) {
			boolean found = true;
			for (Object key : guidMap.keySet()) {
				String keyValue = childGuidMap.get(key);
				if( ! ((String)guidMap.get(key)).equals(keyValue)) {
					found = false;
				}
			}
			if(found) {
				return true;
			}
		}
		return false;
	}

	private String getResolverName(Segment child) {
		return child.getPrev().getServiceName() + child.getServiceName() + "Resolver";
	}
	
	public static SIGSegmentExecutor newExecutor(SIGEntityGateway gateway, Segment accounts) {
		return new SIGSegmentExecutor(gateway, accounts);
	}
	
	public void createChild(Segment child) {
		throw new UnsupportedOperationException();
	}
}
