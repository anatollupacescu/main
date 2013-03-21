package net.eam;

import net.eam.core.Processor;
import net.eam.core.impl.WaitAfterConveyer;
import net.eam.model.IncrementorProcessor;
import net.eam.target.SimpleCounter;

public class MyRunnable implements Runnable {

	private final int sleepTime;
	
	public MyRunnable(int i) {
		sleepTime = i;
	}

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
