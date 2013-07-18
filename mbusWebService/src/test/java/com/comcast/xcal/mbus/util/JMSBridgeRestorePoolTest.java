package com.comcast.xcal.mbus.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.comcast.xcal.activemq.AckPlugin;
import com.comcast.xcal.mbus.config.Config;
import com.comcast.xcal.mbus.config.IMBusWebServiceProperties;
import com.comcast.xcal.mbus.ptp.JMSBridge;

public class JMSBridgeRestorePoolTest {
	
	private static BrokerService broker;
	private boolean brokerRuns= false;
	private final String QUEUE = "TEST_QUEUE_FOR_JMS_BRIDGE";
	private String inflightQueueName = "XCAL.MBUS.MESSAGES.INFLIGHT";
	private final long READ_TIMEOUT = 50; // ms
    private JMSBridge jmsBridge;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    
    @BeforeClass
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }
    
	@Test
 	public void testBrigdeWorksAfterReset() throws Exception {
        startRealBroker();
        jmsBridge = getJMSBridge(new MockProps());
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
		
 		System.out.println("ok");
 		
 		/*Restarting broker, simulating failed situation*/
 		restartBroker();
 		
 		for (int i = 0; i < 100; i++){
 	 		try {
 	 	 		jmsBridge.receiveMessage("QUEUE", QUEUE, READ_TIMEOUT);
 			} catch (Exception e) {
 			}	
 		}
 		
 		String result = outContent.toString();
 		Assert.assertTrue(result.contains("Caught exception trying rollback() when putting session back into the pool, will invalidate. javax.jms.IllegalStateException: The Session is closed"));
 		Assert.assertTrue(result.contains("javax.jms.IllegalStateException: The Session is closed"));

 	}
 	
    public void startRealBroker() throws Exception {
        broker = new BrokerService();

        broker.addConnector("tcp://127.0.0.1:61616");

        // need for message priorities
        // TODO: other important configuration params need to set up here
        PolicyEntry policy = new PolicyEntry();
        policy.setPrioritizedMessages(true);
        policy.setProducerFlowControl(false);
        policy.setEnableAudit(false);
        
        PolicyMap pMap = new PolicyMap();
        pMap.setDefaultEntry(policy);
        broker.setDestinationPolicy(pMap);

        
        broker.setUseJmx(true);
        broker.deleteAllMessages();
        broker.start();
        broker.waitUntilStarted();
    }
    
    
    public void restartBroker() throws Exception{
    	broker.stop();
    	broker.waitUntilStopped();
        broker.start();
    	broker.waitUntilStarted();
    }
    
    public JMSBridge getJMSBridge(IMBusWebServiceProperties mockProps){
        Config config = new Config();
        WhiteboxImpl.setInternalState(config, "mbusWebServiceProperties", mockProps);
        ActiveMQConnectionFactory connectionFactory = config.initializeActiveMQConnectionFactory();

        return new JMSBridge(mockProps, connectionFactory);
    }
    
}
