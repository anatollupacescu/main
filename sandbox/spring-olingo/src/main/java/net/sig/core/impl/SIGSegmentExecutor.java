package net.sig.core.impl;

import java.util.List;
import java.util.Map;

import net.sig.core.SIGAbstractCacheStore;
import net.sig.core.Segment;
import net.sig.core.SegmentExecutor;

public class SIGSegmentExecutor implements SegmentExecutor {

	private final Segment current;
	private final SIGEntityGateway gateway;
	
	private SIGSegmentExecutor(SIGEntityGateway gateway, Segment segment) {
		this.gateway = gateway;
		this.current = segment;
	}
	
	public Object execute() {
		if(current.hasGuid()) {
			if(current.hasPrev()) {
				Segment parent = current.getPrev();
				SIGSegmentExecutor executor = SIGSegmentExecutor.newExecutor(gateway, parent);
				if(executor.hasChilds(current)) {
					Map guid = current.getGuid();
					return (Map)gateway.getService(current.getServiceName()).load(guid);
				}
			} else {
				return gateway.getService(current.getServiceName()).load(current.getGuid());
			}
		} else {
			if(current.hasPrev()) {
				Segment parent = current.getPrev();
				SIGSegmentExecutor executor = SIGSegmentExecutor.newExecutor(gateway, parent);
				return executor.getChilds(current);
			} else {
				return gateway.getService(current.getServiceName()).loadAll(null);
			}
		}
		
		return null;
	}
	
	public Map getChilds(Segment child) {
		String resolverName = resolverName(child);
		SIGAbstractCacheStore subscriberAccountsResolver = gateway.getService(resolverName);
		SIGAbstractCacheStore targetDAS = gateway.getService(child.getServiceName());
		List<Object> entityIds = (List<Object>) subscriberAccountsResolver.load(current.getGuid());
		return targetDAS.loadAll(entityIds);
	}

	private String resolverName(Segment child) {
		return current.getServiceName() + child.getServiceName() + "Resolver";
	}

	public void createChild(Segment child) {
		throw new UnsupportedOperationException();
	}

	public boolean hasChilds(Segment child) {
		return getChilds(child).values().size() > 0;
	}

	public static SIGSegmentExecutor newExecutor(SIGEntityGateway gateway, Segment accounts) {
		return new SIGSegmentExecutor(gateway, accounts);
	}

}
