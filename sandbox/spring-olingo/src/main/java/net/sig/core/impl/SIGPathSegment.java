package net.sig.core.impl;

import com.google.common.base.Strings;

import net.sig.core.Segment;

public class SIGPathSegment implements Segment {

	private final GenericKey guid;
	private final String serviceName;
	private Segment prev;
	
	private SIGPathSegment(String serviceName) {
		this.guid = null;
		this.serviceName = serviceName;
	}

	private SIGPathSegment(String serviceName, GenericKey guid) {
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

	public GenericKey getGuid() {
		return guid;
	}

	public boolean hasGuid() {
		return guid != null;
	}

	public static Segment newSegment(String service) {
		return new SIGPathSegment(service);
	}
	
	public static Segment newSegment(String service, GenericKey key) {
		return new SIGPathSegment(service, key);
	}

	@Override
	public String toString() {
		return String.format("/%s(%s)", serviceName, (guid == null ? "" : guid));
	}
}
