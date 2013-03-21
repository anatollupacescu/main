package net.eam;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExecutionAccessManager {

	private final static Map<String, AtomicBoolean> holder = new HashMap<String, AtomicBoolean>();
	private final static AtomicBoolean canAsk = new AtomicBoolean(true);

	public static boolean canEnter(String processor) {
		if(!canAsk.compareAndSet(true, false)) return false;
	
		AtomicBoolean canEnter = getLock(processor);
		boolean value = canEnter.compareAndSet(true, false); 
		
		if(value == false)
			allowAsk();
		
		return value;
	}
	
	public static boolean allowAsk() {
		return canAsk.compareAndSet(false, true);
	}
	
	public static void allowEnter(String method) {
		AtomicBoolean canEnter = getLock(method);
		canEnter.set(true);
	}
	
	private static AtomicBoolean getLock(String method) {
		AtomicBoolean canEnter = holder.get(method);
		if(canEnter == null) {
			canEnter = new AtomicBoolean(true);
			holder.put(method, canEnter);
		}
		return canEnter;
	}
}
