package net.sig.core;

import net.sig.core.impl.GenericKey;

public interface Segment {

	public boolean hasGuid();
	
	public GenericKey getGuid();
	
	public String getServiceName();
	
	public boolean hasPrev();
	
	public Segment getPrev();
	
	public void setPrev(Segment segment);
}
