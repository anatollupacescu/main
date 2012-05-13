package sandbox.eam.test;

import sandbox.eam.core.Processor;
import sandbox.eam.core.impl.WaitAfterConveyer;
import sandbox.eam.test.model.IncrementorProcessor;
import sandbox.eam.test.target.SimpleCounter;

public class MyRunnable implements Runnable {

	private final int sleepTime;
	
	public MyRunnable(int i) {
		sleepTime = i;
	}

	@Override
	public void run() {
		
		double hits = 0;
		
		Processor p = new IncrementorProcessor(SimpleCounter.class.getName(), sleepTime);
		
		while (true) {
			try {
				WaitAfterConveyer.execute(p);
				hits++;
			} catch (Exception e) {
				System.out.println(e.getMessage() + "\nHits " + hits);
				return;
			}
		}
	}
}
