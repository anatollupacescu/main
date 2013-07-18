package com.comcast.xcal.mbus.resource;

import org.testng.annotations.BeforeMethod;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Test utilize 2 instances on web-service (WS) 
 * connected to network of brokers.
 * Check for possibility of making different actions
 * on different WS instances and makes sure
 * that actions  performed correctly.
 * 
 * Summary table of what is checked
 * 
 * Acknowledge plugin (possibility to ack on any WS):
 * testSend1Ack1 : send message on 1-st WS and acknowledge on 1-st WS
 * testSend1Ack2 : send message on 1-st WS and acknowledge on 2-nd WS
 * testSend2Ack1 : send message on 2-nd WS and acknowledge on 1-st WS
 * testSend2Ack2 : send message on 2-nd WS and acknowledge on 2-nd WS
 * 
 * Having local consumer on the broker(s)
 * may cause situation when message reading 
 * is impossible on another broker
 * so "message get stuck".
 * 
 * Summary table of what is checked:
 * 
 * testMessageStuck11 : send message on 1-st WS and acknowledge on 1-st WS
 * testMessageStuck12 : send message on 1-st WS and acknowledge on 1-st WS
 * testMessageStuck21 : send message on 1-st WS and acknowledge on 1-st WS
 * testMessageStuck22 : send message on 1-st WS and acknowledge on 1-st WS
 * 
 * @author kchumichkin
 *
 */

public class DistributedTest {

	private String wsUrl1 = "http://localhost:8080/MBusWebService-CCP-LATEST-SNAPSHOT1";
	private String wsUrl2 = "http://localhost:8080/MBusWebService-CCP-LATEST-SNAPSHOT2";
	private String queueName = "XCAL.XBO.DEV.TO.ACCOUNTUPDATER_1.0"; 
	

	@Test(groups="dist", invocationCount=10)
	public void testSend1Ack1() {
		sendReceiveAck(wsUrl1, wsUrl1, "11");
	}

	@Test(groups="dist", invocationCount=10)
	public void testSend1Ack2() {
		sendReceiveAck(wsUrl1, wsUrl2, "12");
	}

	@Test(groups="dist", invocationCount=10)
	public void testSend2Ack1() {
		sendReceiveAck(wsUrl2, wsUrl1, "21");
	}

	@Test(groups="dist", invocationCount=10)
	public void testSend2Ack2() {
		sendReceiveAck(wsUrl2, wsUrl2, "22");
	}

	@Test(groups="dist")
	public void testMessageStuck11() {
		checkMessageStuck(wsUrl1, wsUrl1);
	}

	@Test(groups="dist")
	public void testMessageStuck12() {
		checkMessageStuck(wsUrl1, wsUrl2);
	}

	@Test(groups="dist")
	public void testMessageStuck21() {
		checkMessageStuck(wsUrl2, wsUrl1);
	}

	@Test(groups="dist")
	public void testMessageStuck22() {
		checkMessageStuck(wsUrl2, wsUrl2);
	}

	@BeforeMethod
	public void cleanupQueue() {
		System.out.println("=== cleanup");
		
		String msg1 = receiveMessage(wsUrl1, 100);
		while (!isEmpty(msg1)) {
			ackMessage(wsUrl1, getMessageId(msg1));
			msg1 = receiveMessage(wsUrl1, 100);
		}
		if (!isEmpty(msg1)) {
			ackMessage(wsUrl1, getMessageId(msg1));
		}
		
		String msg2 = receiveMessage(wsUrl2, 100);
		while (!isEmpty(msg2)) {
			ackMessage(wsUrl2, getMessageId(msg2));
			msg2 = receiveMessage(wsUrl2, 100);
		}
		if (!isEmpty(msg2)) {
			ackMessage(wsUrl2, getMessageId(msg2));
		}

	}	

	
	private void checkMessageStuck(String sendUrl, String receiveUrl) {

		// reading message will cause that local consumer appear
		String msg1 = receiveMessage(wsUrl1, 100);
		msg1 = receiveMessage(wsUrl2, 100);
		
		// sending message on 1-st url
		String res = sendMessage(sendUrl, "message");
		assert !isEmpty(res);
		
		// trying to read the message on 2-nd url
		String msg2 = receiveMessage(receiveUrl, 500);
		if (isEmpty(msg2))
			msg2 = receiveMessage(receiveUrl, 500); // one more time
		if (isEmpty(msg2))
			msg2 = receiveMessage(receiveUrl, 500); // and one more
		
		assert (!isEmpty(msg2));
	}
	
	private void sendReceiveAck(String urlSend, String urlAck, String message) { 
		// 1 send the message on specified url
		String res = sendMessage(urlSend, message);
		assert !isEmpty(res);
		
		
		// 2 receive messages on both urls
		// make sure that 1 and only 1 received
		String msg1 = receiveMessage(wsUrl1, 100);
		String msg2 = receiveMessage(wsUrl2, 100);
		
		System.out.println("== " + msg1);
		System.out.println("== " + msg2);
		
		
		assert !isEmpty(msg1) || !isEmpty(msg2); // at least 1 is not empty
		assert isEmpty(msg1) || isEmpty(msg2); // at least 1 is empty

		String id = getMessageId( isEmpty(msg1) ? msg2 : msg1);
		assert !isEmpty(id);
		
		
		ackMessage(urlAck, id);
		

		// nothing received on both urls after ack is made on one of them
		/*
		msg1 = receiveMessage(wsUrl1, 100);
		assert isEmpty(msg1);

		msg2 = receiveMessage(wsUrl2, 100);
		assert isEmpty(msg2);
		*/
	}

	private String sendMessage(String baseUrl, String body) {
		URL url;
		try {
			url = new URL(baseUrl + "/message/" + queueName + "?Action=SendMessage&version=1.0&MessageBody=" + body);
		
	        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
	        httpConnection.setRequestMethod("GET");
	        httpConnection.setAllowUserInteraction(false);
	        httpConnection.setDoOutput(false);
	        httpConnection.setRequestProperty("Content-type","text/xml");
	        httpConnection.connect();
	
	        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(httpConnection.getInputStream()) );
	        StringBuilder strBuf = new StringBuilder();
	
	        while( bufferedReader.ready()){
	            strBuf.append(bufferedReader.readLine());
	        }
	        bufferedReader.close();
	        httpConnection.disconnect();
	
	        return strBuf.toString();
        } catch (Exception e) {
        	return null;
        }

	}
	
	private String receiveMessage(String baseUrl, long timeout) {
		URL url;
		try {
			url = new URL(baseUrl + "/message/" + queueName + "?Action=ReceiveMessage&version=1.0&ReadTimeOutValue=" + timeout);
		
	        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
	        httpConnection.setRequestMethod("GET");
	        httpConnection.setAllowUserInteraction(false);
	        httpConnection.setDoOutput(false);
	        httpConnection.setRequestProperty("Content-type","text/xml");
	        httpConnection.connect();
	
	        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(httpConnection.getInputStream()) );
	        StringBuilder strBuf = new StringBuilder();
	
	        while( bufferedReader.ready()){
	            strBuf.append(bufferedReader.readLine());
	        }
	        bufferedReader.close();
	        httpConnection.disconnect();
	
	        return strBuf.toString();
        } catch (Exception e) {
        	return null;
        }
	}
	
	private String ackMessage(String baseUrl, String id) {
		URL url;
		try {
			url = new URL(baseUrl + "/message/" + queueName + "?Action=DeleteMessage&version=1.0&MessageId=" + id);
		
	        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
	        httpConnection.setRequestMethod("GET");
	        httpConnection.setAllowUserInteraction(false);
	        httpConnection.setDoOutput(false);
	        httpConnection.setRequestProperty("Content-type","text/xml");
	        httpConnection.connect();
	
	        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(httpConnection.getInputStream()) );
	        StringBuilder strBuf = new StringBuilder();
	
	        while( bufferedReader.ready()){
	            strBuf.append(bufferedReader.readLine());
	        }
	        bufferedReader.close();
	        httpConnection.disconnect();
	
	        return strBuf.toString();
        } catch (Exception e) {
        	return null;
        }
	}
	
	private String getMessageId(String response) {
		try {
			return findWithXPath("//ReceiveMessageResponse/ReceiveMessageResult/Message/MessageId", stringToElement(response));
		} catch (Exception e) {
			return null;
		}
	}
	
	private String getMesageBody(String response) {
			try {
				return findWithXPath("//ReceiveMessageResponse/ReceiveMessageResult/Message/MessageBody", stringToElement(response));
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
