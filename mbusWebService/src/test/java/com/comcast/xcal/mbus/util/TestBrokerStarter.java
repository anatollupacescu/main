package com.comcast.xcal.mbus.util;

import com.comcast.xcal.activemq.AckPlugin;
import com.comcast.xcal.mbus.config.IMBusWebServiceProperties;
import com.comcast.xcal.mbus.config.Config;
import com.comcast.xcal.mbus.ptp.JMSBridge;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.io.IOException;
import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 9/11/12
 * Time: 4:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestBrokerStarter {
	
	private static BrokerService broker;
	
    public static JMSBridge startEmbeddedBroker(IMBusWebServiceProperties mockProps) throws Exception {
        broker = new BrokerService();

        BrokerPlugin[] plugins = {new AckPlugin()};
        broker.setPlugins(plugins);

        broker.addConnector("tcp://127.0.0.1:61616");
        URI vmUri = broker.getBroker().getVmConnectorURI();
        ((MockProps)mockProps).set("xcal.mbus.brokerURL", vmUri.toString());

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

        Config config = new Config();
        WhiteboxImpl.setInternalState(config, "mbusWebServiceProperties", mockProps);
        ActiveMQConnectionFactory connectionFactory = config.initializeActiveMQConnectionFactory();

        return new JMSBridge(mockProps, connectionFactory);
    }
    
    public static void clanupBroker() throws IOException {
    	broker.deleteAllMessages();
    }
}
