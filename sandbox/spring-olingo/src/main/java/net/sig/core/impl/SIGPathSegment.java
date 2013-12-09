package net.sig.core.impl;

import java.util.Map;

import net.sig.core.Segment;

public class SIGPathSegment implements Segment {

	private final Map guid;
	private final String serviceName;
	private Segment prev;
	
	private SIGPathSegment(String serviceName) {
		this.guid = null;
		this.serviceName = serviceName;
	}

	private SIGPathSegment(String serviceName, Map guid) {
		this.guid = guid;
		this.serviceName = serviceName;
	}
	
	public String getServiceName() {
		return serviceName;
	}
	
	public boolean hasPrev() {
		return prev != null;
	}

	public Segment getPrev() {
		return prev;
	}
	
	public void setPrev(Segment segment) {
		this.prev = segment;
	}

	public Map getGuid() {
		return guid;
	}

	public boolean hasGuid() {
		return guid != null;
	}

	public static Segment newSegment(String service) {
		return new SIGPathSegment(service);
	}
	
	public static Segment newSegment(String service, Map guid) {
		return new SIGPathSegment(service, guid);
	}

}
