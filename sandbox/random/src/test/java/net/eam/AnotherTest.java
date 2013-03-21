package net.eam;

import net.eam.core.impl.WaitAfterConveyer;
import net.eam.model.StoreProcessor;
import net.eam.target.SimpleCounter;

public class AnotherTest {

	final static int sleepTime = 10;

	public static void main(String[] args) {
		
		StoreProcessor p = new StoreProcessor(SimpleCounter.class.getName(), sleepTime);

		try {
			new IncrementerTest();
		} catch (Exception e) {
		}

		try {
			WaitAfterConveyer.execute(p);
		} catch (Exception e) {
		}
		System.out.println("p.getResult()" + p.getResult());
	}

}
