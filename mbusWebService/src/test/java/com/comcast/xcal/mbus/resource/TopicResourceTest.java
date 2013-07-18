package com.comcast.xcal.mbus.resource;

import com.comcast.xcal.mbus.config.IMBusWebServiceProperties;
import com.comcast.xcal.mbus.ptp.JMSBridge;
import com.comcast.xcal.mbus.util.MockProps;
import com.comcast.xcal.mbus.util.TestBrokerStarter;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Cookie;
import com.ning.http.client.Response;
import com.ning.http.client.websocket.WebSocket;
import com.ning.http.client.websocket.WebSocketTextListener;
import com.ning.http.client.websocket.WebSocketUpgradeHandler;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

public class TopicResourceTest {


    private JMSBridge jmsBridge;
    private boolean brokerRuns;

//    @BeforeGroups("web")
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

    /* TODO: the callback for this is being fired twice even though only one message is submitted. */
    /* TODO: Commented to fix  106 line */
//    @Test(groups="web")
    public void websocketShouldConnectAndRegisterCallbackForTemporaryTopic() throws Exception {

        int numberOfMessagesToPublish = 1;
        final AtomicInteger messageCount = new AtomicInteger(0);
        final CountDownLatch openLatch = new CountDownLatch(1);
        final CountDownLatch messageLatch = new CountDownLatch(numberOfMessagesToPublish);

        final AsyncHttpClient c = new AsyncHttpClient();

        Response prepareTemporaryTopic = c.preparePut("http://localhost:10189/mbus/topic/TEMPORARY")
                .execute()
                .get();
        List<Cookie> cookies = prepareTemporaryTopic.getCookies();

        final String temporaryTopicName = prepareTemporaryTopic.getResponseBody();

        AsyncHttpClient.BoundRequestBuilder subscribe = c.prepareGet("ws://localhost:10189/mbus/topic/"+temporaryTopicName+"?Action=Subscribe&version=1.0&Protocol=WS&isTemporary=true");
        addCookies(cookies, subscribe);
        WebSocket w = subscribe
                .addHeader("CorrelationId", "TopicResourceTest")
                .execute(new WebSocketUpgradeHandler.Builder().build())
                .get();

        w.addWebSocketListener(new WebSocketTextListener() {

            @Override
            public void onMessage(String message) {
                messageCount.incrementAndGet();
                System.out.println("*** MESSAGE RECEIVED > " + message + " : " + messageCount.get());
                messageLatch.countDown();
                assert message.contains("TESTMESSAGE");
            }

            @Override
            public void onFragment(String s, boolean b) {
            }

            @Override
            public void onOpen(WebSocket webSocket) {
            }

            @Override
            public void onClose(WebSocket webSocket) {
            }

            @Override
            public void onError(Throwable throwable) {
            }
        });

        try {
            assertTrue(w.isOpen());

            for (int i = 0; i < numberOfMessagesToPublish; i++) {
                System.out.println("*** PUBLISHING " + i);
                Response r = publishDefaultMessage(c, temporaryTopicName, i, true);
                assert r.getStatusCode() == 200 : "Fake publish did not return success: " + r.getStatusText();
            }

            assert messageLatch.await(10, TimeUnit.SECONDS) : messageLatch.toString();
            Response r = sendUnsubscribe(c, temporaryTopicName, true, cookies);
            // seem to be getting duplicates on temp topics -- could this be the issue?
            assert messageCount.get() == numberOfMessagesToPublish : "Expected "+ numberOfMessagesToPublish+ " but received " + messageCount.get();

        } catch (InterruptedException iex) {
            fail("Timed out waiting.");
        } finally {
            w.close();
            c.close();
        }

    }


    @Test(groups="web")
    public void websocketShouldConnectAndRegisterCallback() throws Exception {

        final String topicName = "XCAL.XBO.DEV.CHRIS_1.0";
        int numberOfMessagesToPublish = 1;
        final AtomicInteger messageCount = new AtomicInteger(0);
        final CountDownLatch openLatch = new CountDownLatch(1);
        final CountDownLatch messageLatch = new CountDownLatch(numberOfMessagesToPublish);

        final AsyncHttpClient c = new AsyncHttpClient(new AsyncHttpClientConfig.Builder().setFollowRedirects(true).build());

        Response prepareSubscription = c.preparePut("http://localhost:10189/mbus/topic/LONG_LIVED")
                .execute()
                .get();
        final List<Cookie> cookies = prepareSubscription.getCookies();

        AsyncHttpClient.BoundRequestBuilder subscribe = c.prepareGet("ws://localhost:10189/mbus/topic/XCAL.XBO.DEV.CHRIS_1.0?Action=Subscribe&version=1.0&Protocol=WS");
        addCookies(cookies, subscribe);
        WebSocket w = subscribe
                .execute(new WebSocketUpgradeHandler.Builder().build())
                .get();

        w.addWebSocketListener(new WebSocketTextListener() {

            public void onMessage(String message) {
                messageCount.incrementAndGet();
                System.out.println("*** MESSAGE RECEIVED > " + message + " : " + messageCount.get());
                messageLatch.countDown();
                assert message.contains("TESTMESSAGE");
            }

            @Override
            public void onFragment(String s, boolean b) {
            }

            @Override
            public void onOpen(WebSocket websocket) {
                openLatch.countDown();
            }

            @Override
            public void onClose(WebSocket webSocket) {
                try {
                    Response r = sendUnsubscribe(c, topicName, false, cookies);
                } catch (Exception e) {
                    fail("Unable to unsubscribe: " + e);
                }
            }

            @Override
            public void onError(Throwable throwable) {
            }

        });



        try {


            openLatch.await(2, TimeUnit.SECONDS);

            for (int i = 0; i < numberOfMessagesToPublish; i++) {
                System.out.println("*** PUBLISHING " + i);
                Response r = publishDefaultMessage(c, topicName, i, false);
                assert r.getStatusCode() == 200 : "Fake publish did not return success: " + r.getStatusText();
            }

            messageLatch.await(10, TimeUnit.SECONDS);
            assert messageCount.get() == numberOfMessagesToPublish : "Expected "+ numberOfMessagesToPublish+ " but received " + messageCount.get();

        } finally {
            w.close();
            c.close();
        }

    }

    private void addCookies(List<Cookie> cookies, AsyncHttpClient.BoundRequestBuilder request) {
        for (Cookie cookie : cookies) {
            request.addCookie(cookie);
        }
    }

    private Response publishDefaultMessage(AsyncHttpClient c, String topicName, int index, Boolean useCorrelationId) {
        Response r = null;
        try {
            if (useCorrelationId) {
                r = c.preparePost("http://localhost:10189/mbus/topic/" + topicName)
                        .addHeader("CorrelationID", "TopicResourceTest")
                        .addParameter("Message", "TESTMESSAGE["+ index +"]")
                        .execute()
                        .get();
            } else {
                r = c.preparePost("http://localhost:10189/mbus/topic/" + topicName)
                        .addParameter("Message", "TESTMESSAGE["+ index +"] " +  + System.currentTimeMillis())
                        .execute()
                        .get();
            }

        } catch (Exception e) {
            fail("Couldn't publish.");
        }
        return r;
    }

    private Response sendUnsubscribe(AsyncHttpClient c, String topicName, Boolean useHeader, List<Cookie> cookies) {
        Response r = null;
        try {
            AsyncHttpClient.BoundRequestBuilder unsubscribe = c.prepareDelete("http://localhost:10189/mbus/topic/" + topicName);

            if (useHeader) {
                unsubscribe = unsubscribe
                    .addHeader("CorrelationID", "TopicResourceTest");
            }

            if (cookies != null) {
                addCookies(cookies, unsubscribe);
            }

            r =  unsubscribe
                    .addParameter("version", "1.0")
                    .execute()
                    .get();
        } catch (Exception e) {
            fail("Couldn't publish.");
        }
        return r;
    }
}