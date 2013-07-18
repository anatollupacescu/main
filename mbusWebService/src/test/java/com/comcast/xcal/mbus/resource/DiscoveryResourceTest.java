package com.comcast.xcal.mbus.resource;

import org.mockito.internal.util.reflection.Whitebox;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.comcast.xcal.mbus.config.MBusWebServiceProperties;

public class DiscoveryResourceTest {

	DiscoveryResource r = new DiscoveryResource();
	MBusWebServiceProperties p = new MBusWebServiceProperties();
	
	@Test
	public void testBrokerUrlReturnWithJms(){
		Whitebox.setInternalState(r, "mbusWebServiceProperties", new MBusWebServiceProperties());
		String response = r.discoveryService("AccountUpdater", "1.0", null, "JMS");
		Assert.assertNotNull(response);
		Assert.assertTrue(response.contains("<brokerURL>"));
	}
	
	@Test
	public void testBrokerUrlNotReturnedForHttp(){
		Whitebox.setInternalState(r, "mbusWebServiceProperties", new MBusWebServiceProperties());
		String response = r.discoveryService("AccountUpdater", "1.0", null, null);
		Assert.assertNotNull(response);
		Assert.assertFalse(response.contains("<brokerURL>"));
	}
	
}
