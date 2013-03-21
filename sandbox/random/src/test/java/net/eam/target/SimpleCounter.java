package net.eam.target;

import net.eam.IncrementerTest;

public class SimpleCounter {

	private static int i = 0;

	public static void doIncrement(int number) throws Exception {

		if (i == 0) {
			IncrementerTest.getTime();
			System.out.println("started");
		}
		int j = i;
		i += number;
		if (i != j + number) {
			throw new Exception("Exception i=" + i + " instead of " + (j + number));
		}

		double time = (System.nanoTime() - IncrementerTest.getTime()) / 1000000000.0;
		
		if (time > 1) {
			throw new Exception(Thread.currentThread().getName() + " dies with total of " + i);
		}
	}

	public static Integer get() {
		return Integer.valueOf(i);
	}
}
