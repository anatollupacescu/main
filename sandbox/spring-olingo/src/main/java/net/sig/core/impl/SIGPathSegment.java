package net.sig.core.impl;

public class SIGPathSegment {

	private final GenericKey guid;
	private final String serviceName;
	private SIGPathSegment prev;

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

	public SIGPathSegment getPrev() {
		return prev;
	}

	public void setPrev(SIGPathSegment segment) {
		this.prev = segment;
	}

	public GenericKey getGuid() {
		return guid;
	}

	public boolean hasGuid() {
		return guid != null;
	}

	public static SIGPathSegment newSegment(String service) {
		return new SIGPathSegment(service);
	}

	public static SIGPathSegment newSegment(String service, GenericKey key) {
		return new SIGPathSegment(service, key);
	}

	@Override
	public String toString() {
		return String.format("/%s(%s)", serviceName, (guid == null ? "" : guid));
	}
}
