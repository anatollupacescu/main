package com.comcast.xcal.mbus.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.comcast.xcal.mbus.config.IMBusWebServiceProperties;
import com.comcast.xcal.mbus.ptp.JMSBridge;
import com.comcast.xcal.mbus.util.MockProps;
import com.comcast.xcal.mbus.util.TestBrokerStarter;

public class JMSPriorityPerformance {
	private final String QUEUE = "TEST_QUEUE_FOR_JMS_PRIORITY_PERFORMANCE_";
	private final long READ_TIMEOUT = 50; // ms

	private JMSBridge jmsBridge;

	private final int[] messagesInQueues = {5, 50, 500, 5000};
	private final int numOfExperiments = 10;

	// queue name is key, list of results is value
	private Map<String, List<TestResult>> results = new HashMap<String, List<TestResult>>();

	private class TestResult {
		private int writeDelay;
		private int readDelay;
		private int attempts;

		public TestResult(long wd, long rd, int a) {
			this.setWriteDelay((int) wd);
			this.setReadDelay((int) rd);
			this.setAttempths(a);
		}

		private void setAttempths(int attempts) {
			this.attempts = attempts;
		}
		
		public int getAttempts() {
			return attempts;
		}
		
		public int getWriteDelay() {
			return writeDelay;
		}

		public void setWriteDelay(int writeDelay) {
			this.writeDelay = writeDelay;
		}

		public int getReadDelay() {
			return readDelay;
		}

		public void setReadDelay(int readDelay) {
			this.readDelay = readDelay;
		}

		public int getTotalDelay() {
			return readDelay + writeDelay;
		}

		public String toString() {
			return "write " + getWriteDelay() + " read " + getReadDelay()
					+ " total " + getTotalDelay() + " ms " + getAttempts() + " attempts";
		}
	}

	@BeforeGroups(groups = "jmspriorityperf")
	public void startUpBroker() throws Exception {
		IMBusWebServiceProperties mockProps = new MockProps();
		jmsBridge = TestBrokerStarter.startEmbeddedBroker(mockProps);
	}

	private void fillUpQueues() {
		results = new HashMap<String, List<TestResult>>();
		for (int i = 0; i < messagesInQueues.length; i++) {
			int msgNumber = messagesInQueues[i];
			for (int j = 0; j < msgNumber; j++) {
				try {
					jmsBridge.sendTextMessage("QUEUE", getQueueName(msgNumber), null,
							null, 5, "test message with default priority");
				} catch (JMSException e) {
					assert false;
				}
			}
		}
	}

	/**
	 * checks the performance of subsequent prioritized write-read operation
	 */
	@Test(groups = "jmspriorityperf")
	public void testPriorityPerformanceSubsequentWriteRead() {
		try {
			TestBrokerStarter.clanupBroker();
		} catch (IOException e1) {
			assert false;
		}
		fillUpQueues();
		
		for (int j=0; j<this.numOfExperiments; j++) {
			for (int i = 0; i < messagesInQueues.length; i++) {
				int msgNumber = messagesInQueues[i];

				List<TestResult> queueRes = (results.get(getQueueName(msgNumber)) == null) ? 
						new ArrayList<TestResult>() : results.get(getQueueName(msgNumber));
				queueRes.add(measureQueueReadWrite(getQueueName(msgNumber)));
				results.put(getQueueName(msgNumber), queueRes);
			}
		}
	
		displayResults("subsequent");
	}
	
	/**
	 * checks the performance of prioritized N writes and subsequent N reads
	 */
	// @Test(groups = "jmspriorityperf")
	public void testPriorityPerformancOrderedWriteRead() {
		try {
			TestBrokerStarter.clanupBroker();
		} catch (IOException e1) {
			assert false;
		}

		fillUpQueues();
		for (int i = 0; i < messagesInQueues.length; i++) {
			for (int j = 0; j < numOfExperiments; j++) {
				writePriorityMessage(getQueueName(messagesInQueues[i])); 
			}
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
		}
		
		for (int i = 0; i < messagesInQueues.length; i++) {
			for (int j = 0; j < numOfExperiments; j++) {
				TextMessage t = null;
				try {
					long start = System.currentTimeMillis();
					t = (TextMessage) jmsBridge.receiveMessage("QUEUE",
							getQueueName(messagesInQueues[i]), READ_TIMEOUT);
				
					jmsBridge.ackMessage(t.getJMSMessageID());
					long end = System.currentTimeMillis();
					
					TestResult res = new TestResult(0, end - start, 0);
					
					if (!"test message with highest priority".equals(t.getText())) {
						assert false;
					}
					
					
					
					List<TestResult> queueRes = (results.get(getQueueName(messagesInQueues[i])) == null) ? 
							new ArrayList<TestResult>() : results.get(getQueueName(messagesInQueues[i]));
					queueRes.add(res);
					results.put(getQueueName(messagesInQueues[i]), queueRes);
					
				} catch (JMSException e) {
					assert false;
				}
			}
		}
		displayResults("ordered");
	}
	
	private String getQueueName(int messageCount) {
		return QUEUE + messageCount;
	}

	private TestResult measureQueueReadWrite(String queueName) {
		TestResult res = null;

		try {
			
			long writeTime = writePriorityMessage(queueName);
			long middle = System.currentTimeMillis();


			int i=0;
			TextMessage t = null;
			
			for (i=0; i<50000; i++) { 
				
				middle = System.currentTimeMillis();
				t = (TextMessage) jmsBridge.receiveMessage("QUEUE",
						queueName, READ_TIMEOUT);
				jmsBridge.ackMessage(t.getJMSMessageID());
				
				if ("test message with highest priority".equals(t.getText())) {
					break;
				}
			}
			
			if (!"test message with highest priority".equals(t.getText())) {
				System.out.println(" == " + t.getJMSPriority());
				displayResults("error occured");
				assert false; // we didn't get message we need 
			}
			
			long end = System.currentTimeMillis();
			
			res = new TestResult(writeTime, end - middle, i);
			// System.out.println("==== " + queueName + " : " + res);
		} catch (JMSException e) {
			assert false;
		}
		return res;
	}

	private long writePriorityMessage (String queueName) {
		long start = System.currentTimeMillis();
		try {
			jmsBridge.sendTextMessage("QUEUE", queueName, null, null, 9,
					"test message with highest priority");
		} catch (JMSException e) {
			assert false;
		}
		long middle = System.currentTimeMillis();
		
		return middle - start;
	}
	
	private void displayResults(String title) {
		System.out.println(title);
		Iterator it = results.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, ArrayList<TestResult>> pair = 
					(Map.Entry<String, ArrayList<TestResult>>) it.next();
			
			System.out.println("===== " + pair.getKey() + " ====="); // queue name

			int avgWrite = 0, avgRead = 0, avgTotal = 0;

			ArrayList<TestResult> r = (ArrayList<TestResult>) pair.getValue();

			for (TestResult ri : r) {
				System.out.println(ri);
				avgWrite += ri.getWriteDelay();
				avgRead += ri.getReadDelay();
				avgTotal += ri.getTotalDelay();
			}

			avgWrite /= r.size();
			avgRead /= r.size();
			avgTotal /= r.size();

			System.out.println("------------------------------------------");
			System.out.println("write " + avgWrite + " read " + avgRead
					+ " total " + avgTotal + " ms");
			System.out.println("\n\n");

			it.remove(); 
		}
	}
}
