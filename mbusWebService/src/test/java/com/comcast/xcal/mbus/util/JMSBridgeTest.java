package com.comcast.xcal.mbus.util;

import com.comcast.xcal.mbus.config.IMBusWebServiceProperties;
import com.comcast.xcal.mbus.ptp.JMSBridge;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

/**
 * Test for real message delivery via JMSBridge
 * 
 * @author kchumichkin@productengine.com
 */
public class JMSBridgeTest {
	
	private final String QUEUE = "TEST_QUEUE_FOR_JMS_BRIDGE";
	private String inflightQueueName = "XCAL.MBUS.MESSAGES.INFLIGHT";
	private final long READ_TIMEOUT = 50; // ms
	
	private boolean brokerRuns = false;
    private JMSBridge jmsBridge;


    @BeforeGroups(groups="jms")
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

    @Test(groups="jms")
	public void brokerStarted() {
		assert brokerRuns;
	}
	
	@Test(groups="jms", dependsOnMethods="brokerStarted")
	public void testMessageLifeCycle() {
		String message = "test message";
		try {
			// create and send
			jmsBridge.sendTextMessage("QUEUE", QUEUE, message);

			// receive not empty
			Message m1 = jmsBridge.receiveMessage("QUEUE", QUEUE, READ_TIMEOUT);
			assert (m1 != null);

			// received is same as sent
			TextMessage t = (TextMessage) m1;
			assert t.getText().equals(message);
		
			// we do not read anymore, means only once delivered
			Message m2 = jmsBridge.receiveMessage("QUEUE", QUEUE, READ_TIMEOUT);
			assert (m2 == null);
			
			// ack success
			boolean res = jmsBridge.ackMessage(m1.getJMSMessageID());
			assert res;
			
		} catch (JMSException e) {
			// something went wrong
			assert false;
		}
	}

	@Test(groups="jms", dependsOnMethods="brokerStarted")
	public void testReceivedVsSentMessageCount() {
		String message = "test";
		int count = 10;
		try {
			// create and send several messages
			for (int i=0; i<count; i++) {
				jmsBridge.sendTextMessage("QUEUE", QUEUE, message + i);
			}

			// and receive all of them
			int receivedCount = 0;
			Message m;
			while (null != (m= jmsBridge.receiveMessage("QUEUE", QUEUE, READ_TIMEOUT))) {
				jmsBridge.ackMessage(m.getJMSMessageID());
				receivedCount++;
			}

			// count should be the same
			assert receivedCount == count;
			
		} catch (JMSException e) {

			assert false : "Problem : " + e;
		}
	}

	@Test(groups="jms", dependsOnMethods="brokerStarted")
	public void testReceiveAcync() {
		try {
			jmsBridge.sendTextMessage("QUEUE", QUEUE, "test1");
			jmsBridge.sendTextMessage("QUEUE", QUEUE, "test2");
	
			Message m1 = jmsBridge.receiveMessage("QUEUE", QUEUE, READ_TIMEOUT);
			Message m2 = jmsBridge.receiveMessage("QUEUE", QUEUE, READ_TIMEOUT);
			
			assert (m1 != null) && (m2 != null);
			
			assert jmsBridge.ackMessage(m1.getJMSMessageID());
			assert jmsBridge.ackMessage(m2.getJMSMessageID());

		} catch (JMSException e) {
			// something went wrong
			assert false;
		}
	}
	
	@Test(groups="jms", dependsOnMethods="brokerStarted")
	public void testWrongIdAck() {
		try {
			// assert !JMSBridge.getInstance().ackMessage("SOME_WRONG_ID_82173469809asdFSFC5s");
			jmsBridge.ackMessage("SOME_WRONG_ID_82173469809asdFSFC5s");
			assert true;
		} catch (JMSException e) {
			assert false;
		}
	}

	@Test(groups="jms", dependsOnMethods="brokerStarted")
	public void testReadFromEmptyQueue() {
		try {
			assert null == jmsBridge.receiveMessage("QUEUE", QUEUE, READ_TIMEOUT);
		} catch (JMSException e) {
			assert false;
		}
	}

	@Test(groups="jms", dependsOnMethods="brokerStarted")
	public void testMessageIsNotInProgressAfterAck() {
		try {
			jmsBridge.sendTextMessage("QUEUE", QUEUE, "test1");
			Message m1 = jmsBridge.receiveMessage("QUEUE", QUEUE, READ_TIMEOUT);
			assert (m1 != null);
			assert jmsBridge.ackMessage(m1.getJMSMessageID());
			
			Message m2 = jmsBridge.receiveMessage("QUEUE", inflightQueueName,
                    "JMSMessageID='" + m1.getJMSMessageID() + "'", READ_TIMEOUT);
			assert (m2 == null);

		} catch (JMSException e) {
			// something went wrong
			assert false;
		}
	}

	@Test(groups="jms", dependsOnMethods="brokerStarted")
	public void testMessageIsInProgressBeforeAck() {
		try {
			jmsBridge.sendTextMessage("QUEUE", QUEUE, "test1");
			Message m1 = jmsBridge.receiveMessage("QUEUE", QUEUE, READ_TIMEOUT);
			assert (m1 != null);
			
			
			Message m2 = jmsBridge.receiveMessage("QUEUE", inflightQueueName,
                    "JMSMessageID='" + m1.getJMSMessageID() + "'", READ_TIMEOUT);
			assert (m2 != null);
			
			jmsBridge.ackMessage(m1.getJMSMessageID()); // just for the case
			jmsBridge.ackMessage(m2.getJMSMessageID()); // just for the case

		} catch (JMSException e) {
			// something went wrong
			assert false;
		}
	}

	@Test(groups="jms", dependsOnMethods="brokerStarted")
	public void testMessageSelectors() {
		try {
			TextMessage sent1 = jmsBridge.sendTextMessage("QUEUE", QUEUE, "test1");
			TextMessage sent2 = jmsBridge.sendTextMessage("QUEUE", QUEUE, "test2");
			TextMessage sent3 = jmsBridge.sendTextMessage("QUEUE", QUEUE, "test3");
			
			TextMessage rcvd2 = (TextMessage) jmsBridge.receiveMessage("QUEUE", QUEUE,
                    "JMSMessageID='" + sent2.getJMSMessageID() + "'", READ_TIMEOUT);
			jmsBridge.ackMessage(sent2.getJMSMessageID());
			assert rcvd2 != null;
			assert rcvd2.getText().equals(sent2.getText());

			TextMessage rcvd3 = (TextMessage) jmsBridge.receiveMessage("QUEUE", QUEUE,
                    "JMSMessageID='" + sent3.getJMSMessageID() + "'", READ_TIMEOUT);
			jmsBridge.ackMessage(sent3.getJMSMessageID());
			assert rcvd3 != null;
			assert rcvd3.getText().equals(sent3.getText());

			TextMessage rcvd1 = (TextMessage) jmsBridge.receiveMessage("QUEUE", QUEUE,
                    "JMSMessageID='" + sent1.getJMSMessageID() + "'", READ_TIMEOUT);
			jmsBridge.ackMessage(sent1.getJMSMessageID());
			assert rcvd1 != null;
			assert rcvd1.getText().equals(sent1.getText());

		} catch (Exception e) {
			assert false;
		}
	}
}
