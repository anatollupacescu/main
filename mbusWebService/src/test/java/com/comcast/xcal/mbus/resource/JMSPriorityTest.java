package com.comcast.xcal.mbus.resource;

import com.comcast.xcal.mbus.config.IMBusWebServiceProperties;
import com.comcast.xcal.mbus.ptp.JMSBridge;
import com.comcast.xcal.mbus.util.MockProps;
import com.comcast.xcal.mbus.util.TestBrokerStarter;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.Random;

public class JMSPriorityTest {
	private final String QUEUE = "TEST_QUEUE_FOR_JMS_BRIDGE";
	private final long READ_TIMEOUT = 50; // ms
	
	private boolean brokerRuns = false;
    private JMSBridge jmsBridge;

	@BeforeGroups(groups="jmspriority")
	public void startUpBroker() throws Exception {
        IMBusWebServiceProperties mockProps = new MockProps();

        try {
            // broker may not be running
            jmsBridge = new JMSBridge(mockProps, null);
        } catch (RuntimeException e) {
            jmsBridge = TestBrokerStarter.startEmbeddedBroker(mockProps);
        }
        brokerRuns = true;
	}
	
	@Test(groups="jmspriority")
	public void brokerStarted() {
		assert brokerRuns;
	}
	
	@Test(groups="jmspriority")
	public void testPriority() {
		
		Random generator2 = new Random( System.currentTimeMillis() );
		try {
			// send 100 messages with random priority, from 0 to 9
			for (int i=0; i<100; i++) {
				int rnd = generator2.nextInt(10);
				jmsBridge.sendTextMessage("QUEUE", QUEUE, null, null, rnd, String.valueOf(rnd));
			}

			// and receive them in 10 portions, 10 each
			int[] prioritySum = new int[10];
			
			for (int i=0; i<10; i++) {
				for (int j=0; j<10; j++) {
					TextMessage t = (TextMessage) jmsBridge.receiveMessage("QUEUE", QUEUE, READ_TIMEOUT);
					prioritySum[i] += Integer.valueOf(t.getText());
				}

				// System.out.println(" ==== " + i + " " + prioritySum[i]);
			}
			
			// and make sure that summary of priorities in each portion is ordered down
			for (int i=1; i<10; i++) {
				assert (prioritySum[i-1] > prioritySum[i]);
			}
			
		} catch (JMSException e) {
			assert false;
		}
	}
}
