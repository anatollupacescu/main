package com.comcast.xcal.mbus.resource;

import com.comcast.xcal.mbus.ptp.JMSBridge;
import com.comcast.xcal.mbus.util.TestBrokerStarter;
import com.comcast.xcal.mbus.config.IMBusWebServiceProperties;
import com.comcast.xcal.mbus.util.MockProps;
import org.powermock.reflect.Whitebox;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.testng.AssertJUnit.fail;

public class CorrelationIdTest {

    private boolean brokerRuns = false;
	JMSBridge jmsBridge;

    @BeforeMethod(groups="unit")
	public void init() throws Exception{


        IMBusWebServiceProperties mockProps = new MockProps();

        try {
            // broker may not be running
            jmsBridge = new JMSBridge(mockProps, null);
        } catch (RuntimeException e) {
            jmsBridge = TestBrokerStarter.startEmbeddedBroker(mockProps);
        }
        brokerRuns = true;
	}
	
//	@Test(groups="unit")
	public void testNoCorrelationID() throws JMSException{
		TextMessage m = jmsBridge.sendTextMessage("QUEUE", "someDest", "someMessage", null);
		Assert.assertNull(m.getJMSCorrelationID());
	}
	
//	@Test(groups="unit")
	public void testWithCorrelationID() throws JMSException{
		TextMessage m = jmsBridge.sendTextMessage("QUEUE", "someDest", "someMessage", "correlationID");
		Assert.assertEquals(m.getJMSCorrelationID(), "correlationID");
	}
	
//	@Test(groups="unit")
	public void testWSwithCorrelationId() throws Exception{
		
        JMSBridge jms = mock(JMSBridge.class);

        QueueResource ws = new QueueResource();
	    Whitebox.setInternalState(QueueResource.class, "jmsBridge", jms);

        fail("Need to add correlationId back in...");
        /*
		ws.RequestHandlerPostAdapter("somequeue",
                null, null, null, null, null, null, null, null,
                "SendMessage", "1.0", null, "600", "600", null, "message", "correlationID");
		verify(jms).sendTextMessage(anyString(), anyString(), anyString(), eq("correlationID"));
		*/
	}

//	@Test(groups="unit")
	public void testWSwithNOCorrelationId() throws Exception{

        JMSBridge jms = mock(JMSBridge.class);

        QueueResource ws = new QueueResource();
        Whitebox.setInternalState(QueueResource.class, "jmsBridge", jms);

        fail("Need to add correlationId back in...");
        /*
		ws.RequestHandlerPostAdapter("somequeue", 
				null, null, null, null, null, null, null, null,
				"SendMessage", "1.0", null, "600", "600", null, "message", null);
		verify(jms).sendTextMessage(anyString(), anyString(), anyString(), (String)eq(null));
		*/
	}
	
}
