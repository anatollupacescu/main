package com.comcast.xcal.mbus.client;

import org.springframework.util.Assert;
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
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 7/1/12
 * Time: 8:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpClientTest {
    int numberOfQueues = 10;
    protected final String baseUrlStringFormat = "http://localhost:10189/mbus/queue/%s?Action=%s&version=1.0";
    protected final String queueNameFormat = "WASH.TEST.QUEUE_1.0.%d";
    protected String testMessageEncoded = "%3C%3Fxml+version%3D%221.0%22+encoding%3D%22UTF-8%22+standalone%3D%22no%22%3F%3E%3CEntitlementEvent%3E%3CEventHeader%3E%3CTrackingId%3Ecc078f6d-4a0e-4a91-a51d-2eebf24704cc%3C%2FTrackingId%3E%3CEventDate%3E2012-06-07T07%3A33%3A29.329-04%3A00%3C%2FEventDate%3E%3CEventName%3EEntitlementUpdate%3C%2FEventName%3E%3CEventType%3EAccountProduct%3C%2FEventType%3E%3CEventAction%3EUpdate%3C%2FEventAction%3E%3CEventSource%3EEntitlement%3C%2FEventSource%3E%3CLineOfBizHint%3EHIGH_SPEED_DATA-A+VIDEO-A+VO_IP-A%3C%2FLineOfBizHint%3E%3C%2FEventHeader%3E%3CEventBody%3E%3CIdentityList%3E%3CIdentity%3E234627181231052012Comcast.USRIMS%3C%2FIdentity%3E%3C%2FIdentityList%3E%3CAccountList%3E%3CAccount%3E%3CId%3E219229181231052012Comcast.IMS%3C%2FId%3E%3CNumber%3E0501424893703%3C%2FNumber%3E%3C%2FAccount%3E%3C%2FAccountList%3E%3C%2FEventBody%3E%3C%2FEntitlementEvent%3E";

    public int getNumberOfQueues() {
        return numberOfQueues;
    }

    public String getBaseUrlStringFormat() {
        return baseUrlStringFormat;
    }

    public String getQueueNameFormat() {
        return queueNameFormat;
    }

    public String getTestMessageEncoded() {
        return testMessageEncoded;
    }

    protected String makeRequest(URL url) throws Exception {
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

    protected void sendTestMessage(int count) throws Exception {

        String baseQueueName = String.format(queueNameFormat,count);
        String baseUrl = String.format(baseUrlStringFormat, baseQueueName, "SendMessage");

        String sendUrl = baseUrl + String.format("&MessageBody=%s",testMessageEncoded);

        URL sendMessageUrl = new URL(sendUrl);
        String sendMessageResponseString = makeRequest(sendMessageUrl);
        Assert.hasText(sendMessageResponseString);
    }

    protected void deleteTestMessage(int count, String messageId, String ackToken) throws Exception {

        String baseQueueName = String.format(queueNameFormat,count);
        String baseUrl = String.format(baseUrlStringFormat, baseQueueName, "DeleteMessage");

        String deleteUrl = baseUrl + String.format("&MessageId=%s",messageId,ackToken);
//        String deleteUrl = baseUrl + String.format("&MessageId=%s&AckToken=%s",messageId,ackToken);
        URL deleteMessageUrl = new URL(deleteUrl);

        String deleteMessageResponseString = makeRequest(deleteMessageUrl);
        Assert.hasText(deleteMessageResponseString);

        Element deleteMessageResponse = stringToElement(deleteMessageResponseString);
        String success = findWithXPath("//DeleteMessageResponse/ResponseMetadata/RequestStatus", deleteMessageResponse);

        Assert.hasText(success);
        org.testng.AssertJUnit.assertEquals("SUCCESS", success);
    }

}
