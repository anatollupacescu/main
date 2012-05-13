package sandbox.eam.test;

import sandbox.eam.core.impl.WaitAfterConveyer;
import sandbox.eam.test.model.StoreProcessor;
import sandbox.eam.test.target.SimpleCounter;

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
