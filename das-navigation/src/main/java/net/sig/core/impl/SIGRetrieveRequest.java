package net.sig.core.impl;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sig.core.SIGAbstractCacheStore;
import net.sig.core.SIGSegmentRequest;

import com.google.common.collect.ImmutableMap;

public class SIGRetrieveRequest extends SIGSegmentRequest {

	private static final Logger log = Logger.getLogger("SIGSegment");
	
	public SIGRetrieveRequest(SIGEntityGateway gateway, SIGPathSegment current) {
		super(gateway, current);
	}
	
	public Object execute() {
		log.log(Level.INFO, "Executing segment {0}", new Object[] { current.toString() });
		if(current.hasPrev()) {
			log.log(Level.INFO, "Segment {0} has parent {1}, we will execute it", new Object[] { current, current.getPrev() });
			/* will be executing previous segment */
			final SIGRetrieveRequest previousSegmentExecutor = SIGRetrieveRequest.newExecutor(gateway, current.getPrev());
			GenericData prevSegmentResult = (GenericData)previousSegmentExecutor.execute();
			if(prevSegmentResult == null) {
				throw new IllegalArgumentException(String.format("Parent segment %s could not be found", current.getPrev().toString()));
			}
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
		if(childEntityIds.isEmpty()) {
			return ImmutableMap.of();
		}
		if(childEntityIds.size() == 1) {
			GenericKey key = childEntityIds.iterator().next();
			Object relatedEntity = targetDAS.load(key);
			if(relatedEntity == null) {
				return null;
			}
			return ImmutableMap.<GenericKey, GenericData> of(key, (GenericData)relatedEntity);
		}
		@SuppressWarnings("unchecked")
		Map<GenericKey, GenericData> childEntities = (Map<GenericKey, GenericData>)targetDAS.loadAll(childEntityIds);
		return childEntities;
	}
	
	public static SIGRetrieveRequest newExecutor(SIGEntityGateway gateway, SIGPathSegment accounts) {
		return new SIGRetrieveRequest(gateway, accounts);
	}
	
	public void createChild(SIGPathSegment child) {
		throw new UnsupportedOperationException();
	}
}