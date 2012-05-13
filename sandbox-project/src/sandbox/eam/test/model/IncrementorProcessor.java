package sandbox.eam.test.model;

import sandbox.eam.core.Processor;
import sandbox.eam.test.target.SimpleCounter;

public class IncrementorProcessor extends Processor {

	public IncrementorProcessor(String s, int sleep) {
		super(s, sleep);
	}

	@Override
	public void process() throws Exception {
		SimpleCounter.doIncrement(1); 
	}

}
