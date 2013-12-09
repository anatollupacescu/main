package net.sig.core;

import java.util.Map;

public interface Segment {

	public boolean hasGuid();
	
	public Map getGuid();
	
	public String getServiceName();
	
	public boolean hasPrev();
	
	public Segment getPrev();
	
	public void setPrev(Segment segment);
}
