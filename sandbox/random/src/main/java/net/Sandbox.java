package net;

import net.disruptor.BackgroundLogger;

public class Sandbox {

	private final static BackgroundLogger bl = new BackgroundLogger();

	public static void main(String[] args) {
		for (int i = 0; i < 5; i++) {
			bl.log("jora " + i);
		}
		bl.stop();
	}
}
