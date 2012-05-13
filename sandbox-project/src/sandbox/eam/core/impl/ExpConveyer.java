package sandbox.eam.core.impl;

import sandbox.eam.ExecutionAccessManager;
import sandbox.eam.core.Processor;

public class ExpConveyer {

	public static void execute(Processor p) throws Exception {

		while (!ExecutionAccessManager.canEnter(p.toString())) {
			p.sleep();
		}
		
		ExecutionAccessManager.allowAsk();

		try {
			p.process();
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			ExecutionAccessManager.allowEnter(p.toString());
		}
	}
}
