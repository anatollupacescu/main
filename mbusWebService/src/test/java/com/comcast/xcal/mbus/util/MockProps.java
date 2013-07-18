package com.comcast.xcal.mbus.util;

import com.comcast.xcal.mbus.config.IMBusWebServiceProperties;

import java.util.Properties;

public class MockProps implements IMBusWebServiceProperties {
    Properties props;

    public MockProps() {
        props = new Properties();
        create();
    }

	private void create() {
		props.setProperty("XMLTag=", "<?xml version=\"1.0\"?><DiscoveryResponse>");
		props.setProperty("xcal.mbus.brokerURL", "tcp://127.0.0.1:61616");
		props.setProperty("xcal.mbus.env", "dev");
		props.setProperty("queuenameappender","XCAL.XBO.");

		props.setProperty("xcal.mbus.broker.jmx.port","1099");
		props.setProperty("xcal.mbus.destination.consumer.maxActive","15");
		props.setProperty("xcal.mbus.destination.consumer.maxIdle","15");
		props.setProperty("xcal.mbus.destination.consumer.growOnDemand", "true");

		props.setProperty("xcal.mbus.message.readTimeout", "20000");
		props.setProperty("xcal.mbus.message.visibilityTimeout", "15000");
		props.setProperty("xcal.mbus.destination.consumer.minIdle", "5");
		props.setProperty("xcal.mbus.destination.consumer.maxIdleTime", "30000");
		props.setProperty("xcal.mbus.destination.consumer.evictionRunInterval", "10000");

		props.setProperty("xcal.mbus.reaper.expiredInProgressDestinationName", "DLQ.XCAL.MBUS.MESSAGES.INFLIGHT");
		props.setProperty("xcal.mbus.reaper.expiredInProgressDestinationType", "QUEUE");
		props.setProperty("xcal.mbus.reaper.deadLetterDestinationName", "ActiveMQ.DLQ");
		props.setProperty("xcal.mbus.reaper.deadLetterDestinationType", "QUEUE");
		props.setProperty("xcal.mbus.reaper.maxAttempts", "10");
		props.setProperty("xcal.mbus.reaper.timer.delay", "30");
		props.setProperty("xcal.mbus.reaper.timer.interval", "10");

		props.setProperty("xmlEndpointStart","<endPoint>");
		props.setProperty("xmlEndpointEnd","</endPoint>");

		props.setProperty("xmlGetTagStart","<getEndpoint>");
		props.setProperty("xmlGetTagEnd","</getEndpoint>");
		props.setProperty("xmlSetTagStart","<setEndpoint>");
		props.setProperty("xmlSetTagEnd","</setEndpoint>");

		props.setProperty("xmlErrorTagStart","<errorEndpoint>");
		props.setProperty("xmlErrorTagEnd","</errorEndpoint>");
		props.setProperty("xmlTerminator","</DiscoveryResponse>");

		props.setProperty("xcal.mbus.readTimeOutValue","5000");

		props.setProperty("xcal.mbus.sessionCloseTimer","60");

		props.setProperty("xcal.mbus.InitialReDeliveryDelay","5000");
		props.setProperty("xcal.mbus.MaxReDelivery","10");

		props.setProperty("xcal.mbus.jmxHostIP","127.0.0.1");
		props.setProperty("xcal.mbus.jmxHostPort","1099");

        props.setProperty("xcal.mbus.destination.consumer.whenExhaustedAction","0");
        props.setProperty("xcal.mbus.destination.consumer.testsPerEvictionRun","20");

		props.setProperty("xcal.mbus.supportedServicesList","AccountRefreshFeeder_1.0,ReconciliationDetector_1.0,ReconciliationProcessor_1.0,AccountUpdater_1.0,SessionUpdater_1.0,DeviceUpdater_1.0,AccountComparator_1.0,XREPublisher_1.0,EEGDequeuer_1.0");
	}
	
	public void set(String key, String value) {
		props.setProperty(key, value);
	}

    @Override
    public Properties getProperties() {
        return props;
    }
}
