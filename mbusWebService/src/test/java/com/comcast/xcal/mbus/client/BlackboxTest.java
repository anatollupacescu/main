package com.comcast.xcal.mbus.client;

import org.testng.annotations.Test;

import java.util.Random;

/**
 * Precondition - need to start jetty with app deployed...
 */
public class BlackboxTest extends HttpClientTest {
    Random randomGenerator = new Random();

    @Test(invocationCount = 100, threadPoolSize = 50, groups="briefload")
    public void testSends() throws Exception {
        int randomInt = randomGenerator.nextInt(numberOfQueues);
//        sendTestMessage(randomInt);
        sendTestMessage(6);
    }

    /**
     * The session pool tends to keep up if we set the threadPool size here to equal it.
     * We should probably use a setting in production that mimics the number of concurrent consumers we expect.
     *
     * @throws Exception
     */
    @Test(invocationCount = 4, threadPoolSize = 4, groups="briefload")
    public void testProcess() throws Exception {
        for (int i = 0; i < numberOfQueues; i++) {
            ReceiveMessageHelper receiveTestMessage = new ReceiveMessageHelper(this,false).receive(i);
            assert receiveTestMessage != null && receiveTestMessage.getMessageId() != null;
            String messageId = receiveTestMessage.getMessageId();
            String ackToken = receiveTestMessage.getAckToken();

            deleteTestMessage(i, messageId, ackToken);
        }
    }

    @Test(invocationCount=100, threadPoolSize=10)
    public void testSingleQueueProcess() throws Exception {
        ReceiveMessageHelper receiveTestMessage = new ReceiveMessageHelper(this,false).receive(6);
        assert receiveTestMessage != null && receiveTestMessage.getMessageId() != null;
        String messageId = receiveTestMessage.getMessageId();
        String ackToken = receiveTestMessage.getAckToken();

        deleteTestMessage(6, messageId, ackToken);
    }


    /**
     * @throws Exception
     */
    @Test(invocationCount = 20, threadPoolSize = 5, groups="one-off")
    public void testSendReceiveAndAck() throws Exception {

        int i = 0;

        sendTestMessage(i);
        ReceiveMessageHelper receiveTestMessage = new ReceiveMessageHelper(this,false).receive(i);

        String messageId = receiveTestMessage.getMessageId();
        String ackToken = receiveTestMessage.getAckToken();

        deleteTestMessage(i, messageId, ackToken);

    }

    @Test(groups="one-off")
    public void testSendReceiveNoAck() throws Exception {

        sendTestMessage(1);
        ReceiveMessageHelper receiveTestMessage = new ReceiveMessageHelper(this).receive(1);

        // wait 60 secs.
        Thread.sleep(120 * 1000);

        deleteTestMessage(1, receiveTestMessage.getMessageId(), receiveTestMessage.getAckToken());


    }

//    @Test(groups="unit")
//    public void defaultTest() throws Exception {
//        assert true;
//    }
}
