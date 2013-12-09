package net.sig.core;

import java.util.Map;

public interface SegmentExecutor {

	public Object execute();
	
	public boolean hasChilds(Segment child);
	
	public Map getChilds(Segment child);
	
	public void createChild(Segment child);
}
