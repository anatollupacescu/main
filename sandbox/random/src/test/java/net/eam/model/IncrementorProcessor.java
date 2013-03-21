package net.eam.model;

import net.eam.core.Processor;
import net.eam.target.SimpleCounter;

public class IncrementorProcessor extends Processor {

	public IncrementorProcessor(String s, int sleep) {
		super(s, sleep);
	}

	@Override
	public void process() throws Exception {
		SimpleCounter.doIncrement(1); 
	}

}
