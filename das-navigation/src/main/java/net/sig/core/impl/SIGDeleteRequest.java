package net.sig.core.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sig.core.SIGAbstractCacheStore;
import net.sig.core.SIGSegmentRequest;

public class SIGDeleteRequest extends SIGSegmentRequest {

	private static final Logger log = Logger.getLogger("SIGSegment");
	
	public SIGDeleteRequest(SIGEntityGateway gateway, SIGPathSegment segment) {
		super(gateway, segment);
	}
	
	@Override
	public Object execute() {
		log.log(Level.INFO, "Executing segment {0}", new Object[] { current.toString() });
		if(current.hasPrev()) {
			log.log(Level.INFO, "Segment {0} has parent {1}, we will execute it", new Object[] { current, current.getPrev() });
			/* will be executing previous segment */
			final SIGRetrieveRequest previousSegmentExecutor = SIGRetrieveRequest.newExecutor(gateway, current.getPrev());
			GenericData prevSegmentResult = (GenericData)previousSegmentExecutor.execute();
			if(current.hasGuid()) {
				log.log(Level.INFO, "Segment {0} has guid, will check if it is a valid child of previous segment {1}", new Object[] { current,  current.getPrev() });
    			/* will check if current segment is a valid child of the previous */
    			if(hasChild(prevSegmentResult, current)) {
    				log.log(Level.INFO, "Segment {0} is a valid child of previous segment {1}, will return its value", new Object[] { current,  current.getPrev() });
    				/* if it is, then retrieve it */
    				final SIGAbstractCacheStore currentSegmentService = gateway.getService(current.getServiceName());
    				currentSegmentService.erase(current.getGuid());
    				return null;
    			} else {
    				throw new IllegalStateException(String.format("%s is not a valid child of %s", current, current.getPrev()));
    			}
			} else {
				/* will retrieve previous segment's childs */
				log.log(Level.INFO, "Segment {0} does not have guid, will return childs of previous segment {1}", new Object[] {  current, current.getPrev() });
				deleteChilds(current);
				return null;
			}
		} else {
			log.log(Level.INFO, "Reached the root segment {0}, will return its value", new Object[] { current });
			final SIGAbstractCacheStore currentSegmentService = gateway.getService(current.getServiceName());
			if(current.hasGuid()) {
				currentSegmentService.erase(current.getGuid());
				return null;
			} else {
				deleteChilds(current);
				return null;
			}
		}
	}

	private void deleteChilds(SIGPathSegment current2) {
		// TODO Auto-generated method stub
		
	}

}
