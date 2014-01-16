package net.sig.core.impl;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sig.core.SIGAbstractCacheStore;
import net.sig.core.SIGResolverService;
import net.sig.core.SIGSegmentRequest;

public class SIGCreateRequest extends SIGSegmentRequest {

	private static final Logger log = Logger.getLogger("SIGSegment");
	
	public SIGCreateRequest(SIGEntityGateway gateway, SIGPathSegment current) {
		super(gateway, current);
	}

	@Override
	public Object execute() {
		log.log(Level.INFO, "Executing segment {0}", new Object[] { current.toString() });
		if(!current.hasBody()) {
			throw new IllegalArgumentException("Missing body");
		}
		if(!current.hasGuid()) {
			throw new IllegalArgumentException("Missing guid");
		}
		final GenericKey guid = current.getGuid();
		if(guid.isIncompleteKey()) {
			log.log(Level.INFO, "Segment {0} has an incomplete key, will try to infer missing values from parent", new Object[] { current,  current.getPrev() });
    		if(current.hasPrev()) {
    			log.log(Level.INFO, "Segment {0} has parent {1}", new Object[] { current, current.getPrev() });
    			final SIGPathSegment previous = current.getPrev();
    			final SIGRetrieveRequest previousSegmentExecutor = SIGRetrieveRequest.newExecutor(gateway, previous);
    			GenericData prevSegmentResult = (GenericData)previousSegmentExecutor.execute();
    			if(prevSegmentResult == null) {
    				throw new IllegalArgumentException(String.format("Parent segment %s could not be found", current.getPrev().toString()));
    			}
    			final SIGResolverService resolverService = (SIGResolverService)getResolverService(current);
    			final Map<String, String> map = resolverService.getKeyMappingMap();
    			guid.inferValues(prevSegmentResult, map);
			} else {
				throw new IllegalArgumentException("Could not infer missing key values: parent not found");
			}
		}
		final SIGAbstractCacheStore currentSegmentService = gateway.getService(current.getServiceName());
		currentSegmentService.store(guid, current.getBody());
		return null;
	}
}
