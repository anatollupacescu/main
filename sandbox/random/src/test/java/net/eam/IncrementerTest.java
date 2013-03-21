package net.eam;

import java.util.LinkedList;
import java.util.Queue;

public class IncrementerTest {
	
	private final static int size = 10;
	public final static int sleepTime = 100;
	
	private final static class timer {
		public final static double startTime =  System.nanoTime();
	}

	public static double getTime() {
		return timer.startTime;
	}
	
	public static void main(String[] args) {
		new IncrementerTest();
	}
	
	public IncrementerTest() {
		
		Queue<Thread> threads = new LinkedList<Thread>();
		
		for (int i = 0; i < size; i++) {
			threads.add(new Thread(new MyRunnable(sleepTime)));
		}
		
		Thread t = null;
		while((t = threads.poll()) != null) {
			t.start();
		}
		
	}
	
}
