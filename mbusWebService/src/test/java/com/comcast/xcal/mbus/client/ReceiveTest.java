package com.comcast.xcal.mbus.client;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 7/1/12
 * Time: 8:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReceiveTest extends HttpClientTest {

    @org.testng.annotations.Test(invocationCount = 600, threadPoolSize = 2, groups="one-off")
    public void testReceiveAndAck() throws Exception {

        int i = 0; // queueNumber

        ReceiveMessageHelper receiveTestMessage = new ReceiveMessageHelper(this,false).receive(i);

        if (receiveTestMessage != null) {

            String messageId = receiveTestMessage.getMessageId();
            String ackToken = receiveTestMessage.getAckToken();

            deleteTestMessage(i, messageId, ackToken);

        } else {
            org.testng.AssertJUnit.fail("No message found.");
        }


    }

}
