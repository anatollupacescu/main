package net.sig.core.impl;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sig.core.SIGAbstractCacheStore;

import com.google.common.collect.ImmutableMap;

public class SIGSegmentExecutor {

	private static final Logger log = Logger.getLogger("SIGSegment");
	
	private final SIGPathSegment current;
	private final SIGEntityGateway gateway;
	
	private SIGSegmentExecutor(SIGEntityGateway gateway, SIGPathSegment segment) {
		this.gateway = gateway;
		this.current = segment;
	}
	
	public Object execute() {
		log.log(Level.INFO, "Executing segment {0}", new Object[] { current.toString() });
		if(current.hasPrev()) {
			log.log(Level.INFO, "Segment {0} has parent {1}, we will execute it", new Object[] { current, current.getPrev() });
			/* will be executing previous segment */
			final SIGSegmentExecutor previousSegmentExecutor = SIGSegmentExecutor.newExecutor(gateway, current.getPrev());
			GenericData prevSegmentResult = (GenericData)previousSegmentExecutor.execute();
			if(current.hasGuid()) {
				log.log(Level.INFO, "Segment {0} has guid, will check if it is a valid child of previous segment {1}", new Object[] { current,  current.getPrev() });
    			/* will check if current segment is a valid child of the previous */
    			if(hasChild(prevSegmentResult, current)) {
    				log.log(Level.INFO, "Segment {0} is a valid child of previous segment {1}, will return its value", new Object[] { current,  current.getPrev() });
    				/* if it is, then retrieve it */
    				final SIGAbstractCacheStore currentSegmentService = gateway.getService(current.getServiceName());
    				return currentSegmentService.load(current.getGuid());
    			} else {
    				throw new IllegalStateException(String.format("%s is not a valid child of %s", current, current.getPrev()));
    			}
			} else {
				/* will retrieve previous segment's childs */
				log.log(Level.INFO, "Segment {0} does not have guid, will return childs of previous segment {1}", new Object[] {  current, current.getPrev() });
				return getChilds(current);
			}
		} else {
			log.log(Level.INFO, "Reached the root segment {0}, will return its value", new Object[] { current });
			final SIGAbstractCacheStore currentSegmentService = gateway.getService(current.getServiceName());
			if(current.hasGuid()) {
				return currentSegmentService.load(current.getGuid());
			} else {
				return currentSegmentService.loadAll(null);
			}
		}
	}
	
	private Map<GenericKey, GenericData> getChilds(SIGPathSegment child) {
		SIGAbstractCacheStore resolverService = getResolverService(child);
		SIGAbstractCacheStore targetDAS = gateway.getService(child.getServiceName());
		GenericKey parentGuid = child.getPrev().getGuid();
		@SuppressWarnings("unchecked")
		Collection<GenericKey> childEntityIds = (Collection<GenericKey>) resolverService.load(parentGuid);
		if(childEntityIds.size() == 1) {
			GenericKey key = childEntityIds.iterator().next();
			Object relatedEntity = targetDAS.load(key);
			return ImmutableMap.<GenericKey, GenericData> of(key, (GenericData)relatedEntity);
		}
		@SuppressWarnings("unchecked")
		Map<GenericKey, GenericData> childEntities = (Map<GenericKey, GenericData>)targetDAS.loadAll(childEntityIds);
		return childEntities;
	}
	
	private boolean hasChild(GenericData prevResult, SIGPathSegment child) {
		SIGAbstractCacheStore resolverService = getResolverService(child);
		@SuppressWarnings("unchecked")
		Collection<GenericKey> entityIds = (Collection<GenericKey>) resolverService.load(prevResult.getKey());
		return entityIds.contains(current.getGuid());
	}

	private String getResolverName(SIGPathSegment child) {
		return child.getPrev().getServiceName() + child.getServiceName() + "Resolver";
	}
	
	private SIGAbstractCacheStore getResolverService(SIGPathSegment child) {
		String resolerName = getResolverName(child);
		SIGAbstractCacheStore resolverService = gateway.getService(resolerName);
		if(resolverService == null) {
			throw new IllegalStateException("Could not find resolver service for " + resolerName);
		}
		return resolverService;
	}
	
	public static SIGSegmentExecutor newExecutor(SIGEntityGateway gateway, SIGPathSegment accounts) {
		return new SIGSegmentExecutor(gateway, accounts);
	}
	
	public void createChild(SIGPathSegment child) {
		throw new UnsupportedOperationException();
	}
}
