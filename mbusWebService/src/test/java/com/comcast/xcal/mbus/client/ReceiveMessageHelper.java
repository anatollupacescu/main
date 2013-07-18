package com.comcast.xcal.mbus.client;

import org.springframework.util.Assert;
import org.w3c.dom.Element;

import java.net.URL;

public class ReceiveMessageHelper {
    private final HttpClientTest httpClientTest;

    private boolean useAsserts = true;

    public ReceiveMessageHelper(HttpClientTest httpClientTest) {
        this.httpClientTest = httpClientTest;
    }

    public ReceiveMessageHelper(HttpClientTest httpClientTest, boolean useAsserts) {
        this.httpClientTest = httpClientTest;
        this.useAsserts = useAsserts;
    }

    private String messageId;
    private String ackToken;

    public String getMessageId() {
        return messageId;
    }

    public String getAckToken() {
        return ackToken;
    }

    public ReceiveMessageHelper receive(int count) throws Exception {

        String baseQueueName = String.format(httpClientTest.getQueueNameFormat(), count);
        String baseUrl = String.format(httpClientTest.getBaseUrlStringFormat(), baseQueueName, "ReceiveMessage");

        URL receiveMessageUrl = new URL(baseUrl);
        String receiveMessageResponseString = httpClientTest.makeRequest(receiveMessageUrl);

        if (useAsserts) {
            Assert.hasText(receiveMessageResponseString);
        } else {
            if (receiveMessageResponseString == null || receiveMessageResponseString.isEmpty()) {
                return null;
            }
        }

        Element receiveMessageResponse = httpClientTest.stringToElement(receiveMessageResponseString);
        messageId = httpClientTest.findWithXPath("//ReceiveMessageResponse/ReceiveMessageResult/Message/MessageId", receiveMessageResponse);
        ackToken = httpClientTest.findWithXPath("//ReceiveMessageResponse/ReceiveMessageResult/AckToken", receiveMessageResponse);
        return this;
    }
}