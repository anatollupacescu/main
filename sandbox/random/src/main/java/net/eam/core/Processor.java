package net.eam.core;

public abstract class Processor {

	private final String name;
	private final int sleepTime;

	public Processor(String s, int sleepTimeArg) {
		name = s;
		sleepTime = sleepTimeArg;
	}

	public abstract void process() throws Exception;

	@Override
	public String toString() {
		return name;
	}

	public void sleep() throws InterruptedException {
		Thread.sleep(sleepTime);
	}
}
