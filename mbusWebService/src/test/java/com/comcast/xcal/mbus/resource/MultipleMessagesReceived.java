package com.comcast.xcal.mbus.resource;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.websocket.WebSocket;
import com.ning.http.client.websocket.WebSocketTextListener;
import com.ning.http.client.websocket.WebSocketUpgradeHandler;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/*
 * Test similar to connectors but with dirrect request but dirrect request
 * 
 * 
 */
public class MultipleMessagesReceived {
	private int counter = 0;

	@Test(groups = "web")
	public void testMultiplMessagesReceived() throws InterruptedException,
			ExecutionException, IOException {

		final AsyncHttpClient c = new AsyncHttpClient();

		WebSocket client1 = getClient("corId", c);
		WebSocket client2 = getClient(null, c);

		client1.addWebSocketListener(new WebSocketTextListener() {


			@Override
			public void onOpen(WebSocket arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(Throwable arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onClose(WebSocket arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMessage(String arg0) {
				counter++;
				Assert.assertEquals(counter, 1);

			}

			@Override
			public void onFragment(String arg0, boolean arg1) {
				// TODO Auto-generated method stub

			}
		});

		client2.addWebSocketListener(new WebSocketTextListener() {

			@Override
			public void onOpen(WebSocket arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(Throwable arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onClose(WebSocket arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMessage(String arg0) {
				counter++;
				Assert.assertEquals(counter, 1);
			}

			@Override
			public void onFragment(String arg0, boolean arg1) {
				// TODO Auto-generated method stub

			}
		});

		c.preparePost("http://localhost:10189/mbus/cns/topic/Topic")
				.addHeader("CorrelationID", "TopicResourceTest")
				.addParameter("Message", "TESTMESSAGE").execute().get();
	}

	private WebSocket getClient(String string, AsyncHttpClient c) throws InterruptedException, ExecutionException, IOException {
		WebSocket client;

		if (string == null) {
			client = c
					.prepareGet(
							"ws://localhost:10189/mbus/cns/topic/Topic?Action=Subscribe&version=1.0&Protocol=WS&isTemporary=true")
					.addHeader("CorrelationId", "TopicResourceTest")
					.execute(new WebSocketUpgradeHandler.Builder().build())
					.get();
		} else {
			client = c
					.prepareGet(
							"ws://localhost:10189/mbus/cns/topic/Topic?Action=Subscribe&version=1.0&Protocol=WS&isTemporary=true")
					.addHeader("CorrelationId", string)
					.execute(new WebSocketUpgradeHandler.Builder().build())
					.get();
		}
		return client;
	}
}
