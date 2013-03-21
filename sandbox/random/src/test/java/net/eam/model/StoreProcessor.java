package net.eam.model;

import net.eam.core.Processor;
import net.eam.target.SimpleCounter;

public class StoreProcessor extends Processor {

	public StoreProcessor(String s, int sleepTimeArg) {
		super(s, sleepTimeArg);
	}

	private Integer result1 = null;
	private Integer result2 = null;
	
	@Override
	public void process() throws Exception {
		SimpleCounter.doIncrement(1); 		
		result1 = SimpleCounter.get();
		sleep();
		SimpleCounter.doIncrement(1); 
		result2 = SimpleCounter.get();
	}

	public String getResult() {
		while(result1 == null || result2 == null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return result1 + " " + result2;
	}

}
