package com.comcast.xcal.mbus.resource;

import com.comcast.xcal.mbus.config.IMBusWebServiceProperties;
import com.comcast.xcal.mbus.ptp.JMSBridge;
import com.comcast.xcal.mbus.util.MockProps;
import com.comcast.xcal.mbus.util.TestBrokerStarter;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;

public class QueueResourceTest {

    private static final String CQS_PATH_PREFIX = DiscoveryResource.CQS_PATH;
    private static final String URL = "http://localhost:10189/mbus/";


    private WebConversation conversation = new WebConversation();

    private JMSBridge jmsBridge;
    private boolean brokerRuns;

	@BeforeGroups("web")
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

	
	/**
	 * Checks expected error response from the webservice
	 */
	@Test(groups="web")
	public void testServletRuns() throws Exception {
        WebResponse response = conversation.getResponse(URL + CQS_PATH_PREFIX + "somequeue");
		String responseMsg = response.getText();
		assert responseMsg.contains("ErrorResponse");
	}

	/**
	 * send a message and receive it right after that
	 * compare the results
	 */
	@Test(groups="web")
	public void testSendMessage() throws Exception {
		String body = "body"; 
		String queueName = "test";

        GetMethodWebRequest request = new GetMethodWebRequest(URL + CQS_PATH_PREFIX + queueName);
        request.setParameter("Action", "SendMessage");
        request.setParameter("version", "1.0");
        request.setParameter("MessageBody", body);

        WebResponse response = conversation.getResponse(request);

		assert !isEmpty(response.getText());

        GetMethodWebRequest request1 = new GetMethodWebRequest(URL + CQS_PATH_PREFIX + queueName);
        request1.setParameter("Action", "ReceiveMessage");
        request1.setParameter("version", "1.0");
        request1.setParameter("ReadTimeOutValue", "500");

        WebResponse response1 = conversation.getResponse(request1);

        assert body.equals(getMessageBody(response1.getText()));
	}

	@Test(groups="web")
	public void testSendMessagePost() throws Exception {

		String body = "body"; 
		String queueName = "test";
		
	    PostMethodWebRequest request = new PostMethodWebRequest(URL + CQS_PATH_PREFIX + queueName);
        request.setParameter("Action", "SendMessage");
        request.setParameter("version", "1.0");
        request.setParameter("MessageBody", body);

        WebResponse response = conversation.getResponse(request);

        assert !isEmpty(response.getText());

        GetMethodWebRequest request1 = new GetMethodWebRequest(URL + CQS_PATH_PREFIX + queueName);
        request1.setParameter("Action", "ReceiveMessage");
        request1.setParameter("version", "1.0");
        request1.setParameter("ReadTimeOutValue", "500");

        WebResponse response1 = conversation.getResponse(request1);

		assert body.equals(getMessageBody(response1.getText()));

	}

	// TODO: move to utils
	private String getMessageBody(String response) {
			try {
				return findWithXPath("//ReceiveMessageResponse/ReceiveMessageResult/Message/Body", stringToElement(response));
			} catch (Exception e) {
				return null;
			}
		}
	
    protected String findWithXPath(String xpathExpressionString, Element element) throws Exception {

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression xPathExpression = xpath.compile(xpathExpressionString);
        Object result = xPathExpression.evaluate(element);

        return (String) result;

    }
    
    protected Element stringToElement(String str) {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        Element ele = null;
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new InputSource(new StringReader(str)));
            ele = doc.getDocumentElement();
        } catch (ParserConfigurationException e) {
        } catch(SAXException e) {
        } catch (IOException e) {
        }
        return ele;
    }
    
    private boolean isEmpty(String s) {
    	if (s==null) return true;
    	if (s.equals("")) return true;
    	if (s.trim().equals("")) return true;
    	
    	return false;
    }

}