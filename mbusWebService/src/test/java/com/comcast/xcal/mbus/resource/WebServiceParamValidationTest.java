package com.comcast.xcal.mbus.resource;

import com.comcast.xcal.mbus.config.IMBusWebServiceProperties;
import com.comcast.xcal.mbus.util.MockProps;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class WebServiceParamValidationTest {
	
	protected QueueResource ws;

    @BeforeMethod(groups="unit")
    public void setUp() throws Exception {

        IMBusWebServiceProperties mockProps = new MockProps();

		ws = new QueueResource() {

			@Override
			protected String writeMessage(String queueName,String message, String clientId) {
				return "writeMessage";
			}

			@Override
			protected String writeMessage(String queueName,String message, String clientId, String correlationId, String replyTo, boolean shouldRetry) {
				return "writeMessage";
			}

			@Override
			protected String readMessage(String queueName, int timeoutVal, String clientId) {
				return "readMessage";
			}

			@Override
			protected String deleteMessage(String queueName,String messageId, String clientId) {
				return "deleteMessage";
			}
		};
        WhiteboxImpl.setInternalState(ws, mockProps);
        WhiteboxImpl.setInternalState(ws,"queueName", "testQueue");
    }

	// == common ==
    @Test(groups="unit")
	public void testEmptyParams() {
		assert ws.RequestHandler(null, null, null, null, null, null, null, null, null, null)
				.contains("ErrorResponse");
	}

    @Test(groups="unit")
	public void testWrongAction() {
		assert ws.RequestHandler("WrongAction", "version",
				"messageId", "timeout", "1000", "clientId",
				"message", null, null, null).contains("ErrorResponse");
	}
	
	// == ReceiveMessage ==
    @Test(groups="unit")
	public void testReceiveMessageOK() {
		assert ws.RequestHandler("ReceiveMessage", "version",
				"messageId", "timeout", "1000", "clientId",
				"message", null, null, null).equals("readMessage");

		assert ws.RequestHandler("ReceiveMessage", "version",
				null, null, "1000", null, null, null, null, null)
				.equals("readMessage");
	}

    @Test(groups="unit")
	public void testReceiveMessageMissedQueueName() {
        ws.queueName = null;
		assert ws.RequestHandler("ReceiveMessage", "version",
				null, null, "1000", null, null, null, null, null)
				.contains("ErrorResponse");
	}

    @Test(groups="unit")
	public void testReceiveMessageMissedVersion() {
		assert ws.RequestHandler("ReceiveMessage", null,
				null, null, "1000", null, null, null, null, null)
				.contains("ErrorResponse");
	}

    // if properties absent will fail, that's why MockProps are used
    @Test(groups="unit")
	public void testReceiveMessageMissedTimeout() {
		assert ws.RequestHandler("ReceiveMessage", null,
				null, null, null, null, null, null, null, null)
				.contains("ErrorResponse");
	}

	// == SendMessage ==
    @Test(groups="unit")
	public void testSendMessageOK() {
		assert ws.RequestHandler("SendMessage", "version",
				"messageId", "timeoutval", "readTimeOutVal", "clientId",
				"message", "cor", "some", null).equals("writeMessage");

		assert ws.RequestHandler("SendMessage", "version",
				null, null, null, null, "message", null, null, null)
				.equals("writeMessage");
	}

    @Test(groups="unit")
	public void testSendMessageMissedQueueName() {
        ws.queueName = null;
		assert ws.RequestHandler("SendMessage", "version",
				null, null, null, null, "message", null, null, null)
				.contains("ErrorResponse");
	}

    @Test(groups="unit")
	public void testSendMessageMissedVersion() {
		assert ws.RequestHandler("SendMessage", null,
				null, null, null, null, "message", null, null, null)
				.contains("ErrorResponse");
	}

    @Test(groups="unit")
	public void testSendMessageMissedMessage() {
		assert ws.RequestHandler("SendMessage", "version",
				null, null, null, null, null, null, null, null)
				.contains("ErrorResponse");
	}

	// == DeleteMessage ==
    @Test(groups="unit")
	public void testDeleteMessageOK() {
		assert ws.RequestHandler("DeleteMessage", "version",
				"messageId", "timeoutval", "readTimeOutVal", "clientId",
				"message", null, null, null).equals("deleteMessage");

		assert ws.RequestHandler("DeleteMessage", "version",
				"messageId", null, null, null, null, null, null, null)
				.equals("deleteMessage");
	}

    @Test(groups="unit")
	public void testDeleteMessageMissedQueueName() {
        ws.queueName = null;
		assert ws.RequestHandler("DeleteMessage", "version",
				"messageId", null, null, null, null, null, null, null)
				.contains("ErrorResponse");
	}

    @Test(groups="unit")
	public void testDeleteMessageMissedVersion() {
		assert ws.RequestHandler("DeleteMessage", null,
				"messageId", null, null, null, null, null, null, null)
				.contains("ErrorResponse");
	}

    @Test(groups="unit")
	public void testDeleteMessageMissedMessageId() {
		assert ws.RequestHandler("DeleteMessage", "version",
				null, null, null, null, null, null, null, null)
				.contains("ErrorResponse");
	}
}
