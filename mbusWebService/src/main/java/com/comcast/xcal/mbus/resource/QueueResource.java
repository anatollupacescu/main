package com.comcast.xcal.mbus.resource;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comcast.xcal.mbus.config.IMBusWebServiceProperties;
import com.comcast.xcal.mbus.constant.Constants;
import com.comcast.xcal.mbus.ptp.IJMSBridge;
import com.comcast.xcal.mbus.util.AuditLoggingUtil;
import com.comcast.xcal.mbus.util.MBusWebServiceUtil;

/**
 * Class to be mapped for working with JMS queues. 
 * Describes action:</br>
 *  - put message</br>
 *  - get message</br>
 *  - delete message<br>
 *
 */
@Path("/queue/{queueName}")
@Component
@Scope("request")
public class QueueResource {
	
	private static Logger log = LoggerFactory.getLogger("mbusWebServiceLogger");

    @Inject
    IMBusWebServiceProperties mbusWebServiceProperties;

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    @PathParam("queueName")
    String queueName;


    @Inject
    IJMSBridge jmsBridge;

	/**
	 * 
	 * @param queueName - name of the queue to work with
	 * @param getAction - send\receive\delete
	 * @param getVersion - 1.0 or 1.1
	 * @param getMessageId - JMS message id 
	 * @param getTimeoutval - interval for connection
	 * @param getReadTimeOutVal - reading interval
	 * @param getClientId - id of the client 
	 * @param getMessage - actual message
	 * @param action - send\receive\delete
	 * @param version - 1.0 or 1.1
	 * @param messageId - JMS message id 
	 * @param timeoutval -  reading interval
	 * @param readTimeOutVal - interval for connection
	 * @param clientId - id of the client 
	 * @param message - actual message
	 * @param correlationId - correlation id of the message
	 * @param replyTo - identifier of the sender
	 * @param priority - JMS priority of the messge
	 * @return String xml
	 */
	@POST
	@Produces({"application/xml"})
	public String RequestHandlerPostAdapter(
			@PathParam("queueName") String queueName,
			@QueryParam("Action") String getAction,
			@QueryParam("version") String getVersion,
			@QueryParam("MessageId") String getMessageId,
			@QueryParam("VisibilityTimeout") String getTimeoutval,
			@QueryParam("ReadTimeOutValue") String getReadTimeOutVal,
			@QueryParam("ClientID") String getClientId,
			@QueryParam("MessageBody") String getMessage,

			@FormParam("Action") String action,
			@FormParam("version") String version,
			@FormParam("MessageId") String messageId,
			@FormParam("VisibilityTimeout") String timeoutval,
			@FormParam("ReadTimeOutValue") String readTimeOutVal,
			@FormParam("ClientID") String clientId,
			@FormParam("MessageBody") String message,

			@HeaderParam("CorrelationId") String correlationId,
            @HeaderParam("ReplyTo") String replyTo,
            @HeaderParam("Priority") final String priority) {
		action = (action != null) ? action : getAction;
		version = (version != null) ? version : getVersion;
		messageId = (messageId != null) ? messageId : getMessageId;
		timeoutval = (timeoutval != null) ? timeoutval : getTimeoutval;
		readTimeOutVal = (readTimeOutVal != null) ? readTimeOutVal : getReadTimeOutVal;
		clientId = (clientId != null) ? clientId : getClientId;
		message = (message != null) ? message : getMessage;
		return RequestHandler(action, version, messageId,
				timeoutval, readTimeOutVal, clientId, message, correlationId, replyTo, priority);
	}


	/**
	 * Basic entry point for the queues communication.
	 * Actions to be done:
	 * 		- resolve, is it send or get message operation
	 *		- pass parameters to the method, responsible for processing.
	 * 
	 * @param action - send\receive
	 * @param version - 1.0, 1.1
	 * @param messageId - id of the message
	 * @param timeoutval - timeout parameter
	 * @param readTimeOutVal - readtimeout parameter
	 * @param clientId - id of the client (optional)
	 * @param message - message
	 * @param correlationId - correlationid of message
	 * @param replyTo - replyTo parameter
	 * @param priority - JMS message priority
	 * @return
	 */
	@GET
	@Produces({"application/xml"})
	public String RequestHandler(
			@QueryParam("Action") String action,
			@QueryParam("version") String version,
			@QueryParam("MessageId") String messageId,
			@QueryParam("VisibilityTimeout") String timeoutval,
			@QueryParam("ReadTimeOutValue") String readTimeOutVal,
			@QueryParam("ClientID") String clientId,
			@QueryParam("MessageBody") String message,

            @HeaderParam("CorrelationId") final String correlationId,
            @HeaderParam("ReplyTo") final String replyTo,
            @HeaderParam("Priority") final String priority) {
        Properties props = mbusWebServiceProperties.getProperties();

		String xmlStr = MBusWebServiceUtil.errorMessageBuilder(Constants.ERR_CODE_INTERNAL_ERROR);
		
		if (this.queueName == null) {
			log.error("Queue name cannot be empty");
			return MBusWebServiceUtil.errorMessageBuilder(Constants.ERR_CODE_MISSING_PARAM, "queueName");
		}
		
		if(action == null){
            log.error("Action cannot be empty");
            return MBusWebServiceUtil.errorMessageBuilder(Constants.ERR_CODE_MISSING_PARAM, "Action");
		}

		if(version == null){
            log.error("Version cannot be empty");
            return MBusWebServiceUtil.errorMessageBuilder(Constants.ERR_CODE_MISSING_PARAM, "version");
		}

		if (action.equals("SendMessage")) {
            if (message != null) {
            	if (priority!=null)
                    xmlStr = writeMessage(queueName, message, clientId, correlationId, replyTo, Integer.valueOf(priority), true);
            	else
            		xmlStr = writeMessage(queueName, message, clientId, correlationId, replyTo, true);
            }
		} else if (action.equals("ReceiveMessage")) {
			int timeval;
			if (readTimeOutVal != null) {
				try {
					timeval = Integer.parseInt(readTimeOutVal);
				} catch (Exception e) {
					log.warn("The timeout value provided is invalid; taking the default value");
					timeval = Integer.parseInt(props
							.getProperty("xcal.mbus.readTimeOutValue", "5000"));
				}
			} else {
				timeval = Integer.parseInt(props
						.getProperty("xcal.mbus.readTimeOutValue", "5000"));
			}

			xmlStr = readMessage(queueName, timeval, clientId);
		} else if (action.equals("DeleteMessage")) {
			if (messageId != null) {
				xmlStr = deleteMessage(queueName, messageId, clientId);
			}
		} else if (action.equals("ChangeMessageVisibility")) {
			if ((timeoutval != null) && (messageId != null)) {
				// xmlStr = changeVisibility(queueName,timeoutval,messageId);
			}
		} else {
			log.error("Invalid Action : " + action);
			xmlStr = MBusWebServiceUtil.errorMessageBuilder(Constants.ERR_CODE_INVALID_PARAM_VAL);
		}

		return xmlStr;
	}


	/**
	 * Overloaded method, for writing message to the JMS queue. CorrelationId is not passed
	 * 
	 * @param queueName - name of the queue
	 * @param message - actual message to write
	 * @param clientId - identifier of sender (not necessary)
	 * @return String - result of the operation 
	 */
    protected String writeMessage(String queueName,String message, String clientId) {
		return writeMessage(queueName, message, clientId, null, null, true);
	}
    
    /**
	 * Overloaded method, for writing message to the JMS queue. ReplyTo is not passed
	 * 
	 * @param queueName - name of the queue
	 * @param message - actual message to write
	 * @param clientId - identifier of sender (not necessary)
     * @param correlationId -correlation id of the message.
     * @return String - result of the operation 
     */
    protected String writeMessage(String queueName,String message, String clientId, String correlationId) {
		return writeMessage(queueName, message, clientId, correlationId, null, true);
	}

    /**
	 * Overloaded method, for writing message to the JMS queue.
	 * 
	 * @param queueName - name of the queue
	 * @param message - actual message to write
	 * @param clientId - identifier of sender (not necessary)
     * @param correlationId -correlation id of the message.
     * @param replyTo - reply to name.
     * @param shouldRetry - should writing be retried or not.
     * @return String - result of the operation 
     */
	protected String writeMessage(String queueName,String message, String clientId, String correlationId, String replyTo, 
			boolean shouldRetry) {
		return writeMessage(queueName, message, clientId, correlationId, replyTo, null, true);
	}
	
	/**
	 * Overloaded method, for writing message to the JMS queue.
	 * 
	 * @param queueName - name of the queue
	 * @param message - actual message to write
	 * @param clientId - identifier of sender (not necessary)
     * @param correlationId -correlation id of the message.
     * @param replyTo - reply to name.
	 * @param priority - message priority
	 * @param shouldRetry - should writing be retried or not.
	 * @return String - result of the operation 
	 */
	protected String writeMessage(String queueName,String message, String clientId, String correlationId, String replyTo, 
			Integer priority, boolean shouldRetry) {
		
		String xmlStr = null;
		try{
			
			TextMessage textMsg = jmsBridge.sendTextMessage("QUEUE", queueName, correlationId, replyTo, priority, message);
			log.info("Message: Tracking id [{}] Sending message to {}", new Object[]{MBusWebServiceUtil.getTrackingID(message), queueName});
			AuditLoggingUtil.logSend(MBusWebServiceUtil.getTrackingID(message), message, queueName);
			if(clientId == null){
				clientId = "UNKNOWN CLIENT ID";
			}
			//log.info("Message Written :  ClientID: " + clientId + "   Message: " + textMsg.toString());
			xmlStr = generateSendResponse(textMsg);
			
		} catch (JMSException e) {
			log.error("Error in writing message to queue");
			if(shouldRetry) {
				log.error("Retrying...");
				// if this is first attempt and we recovered the connection - run again
				return writeMessage(queueName, message, clientId, correlationId, replyTo, false);
			}
			
		} catch (Exception e) {
			log.error("Exception in writing message, wrong tracking id {}", e.getMessage());
		}
		return xmlStr.toString();
	}
	
	/**
	 * Method is used to generate response string from the JMS message.
	 * 
	 * @param m - JMS message
	 * @return - string representation of the xml, returned to the user.
	 * @throws UnsupportedEncodingException
	 * @throws JMSException
	 */
	private String generateSendResponse(TextMessage m) throws UnsupportedEncodingException, JMSException {
		String encodedStr = URLEncoder.encode(m.getText().toString(), "UTF-8");
		StringBuilder xmlStr = new StringBuilder();
		xmlStr.append("<SendMessageResponse><SendMessageResult><MD5OfMessageBody>")
		.append(MBusWebServiceUtil.getMD5Hash(encodedStr))
		.append("</MD5OfMessageBody><MessageId>")
    	.append(m.getJMSMessageID())
	    .append("</MessageId></SendMessageResult><ResponseMetadata><RequestId> </RequestId>");
		xmlStr.append(generateCommonResponse(m));
		xmlStr.append("</ResponseMetadata></SendMessageResponse>");

		return xmlStr.toString();
	}

	/*
	 * Helper method, is used for simplifying construction of returning string.
	 * All messages returned have some common parts:</br>
	 * 		- correlation id</br>
	 * 		- reply to</br>
	 * 		- priority </br>
	 * @param m - JMS message
	 * @return
	 * @throws JMSException
	 */
	private String generateCommonResponse(TextMessage m)
			throws JMSException {
		StringBuilder xmlStr= new StringBuilder();
		if (m.getJMSCorrelationID() != null){
			xmlStr.append("<CorrelationID>")
	        .append(m.getJMSCorrelationID())
	        .append("</CorrelationID>");
		}
		if (m.getStringProperty("replyTo") != null ){
			xmlStr.append("<ReplyTo>")
	        .append(m.getStringProperty("replyTo"))
	        .append("</ReplyTo>");
		}
		xmlStr.append("<Priority>")
        .append(m.getJMSPriority())
        .append("</Priority>");
		return xmlStr.toString();
	}

	/**
	 * Overloaded method of readMessage() method with default value of the retry operation
	 * set to true, which means, that in case of some exception, program will try to read message one more time.
	 * 
	 * @param queueName - name of the queue
	 * @param timeoutVal - timeout interval
	 * @param clientId - id of the user.
	 * @return String - result of the operation 
	 */
	protected String readMessage(String queueName, int timeoutVal, String clientId) {
		return readMessage(queueName, timeoutVal, clientId, true);
	}
	
	/**
	 * Read message from the JMS queue.
	 * 
	 * @param queueName - name of the queue to read from
	 * @param timeoutVal - timeout interval
	 * @param clientId - id of the user.
	 * @param shouldRetry - should delete operation be retried in case of exceptions.
	 * @return
	 */
	protected String readMessage(String queueName, int timeoutVal, String clientId, boolean shouldRetry) {
		String xmlStr = null;
		try{			
			 
			TextMessage m = (TextMessage)jmsBridge.receiveMessage("QUEUE", queueName, timeoutVal);
			
			if( m!= null){
				xmlStr = generateReceiveResponse(m);
				
				if(clientId == null){
					clientId = "UNKNOWN CLIENT ID";
				}
				//log.info("Message Read: ClientID: " + clientId + "     Message: " + m.toString());
				// IS IS ENOUGH LOG JUST ONE STATEMENT? 
//				AuditLoggingUtil.logReceive(MBusWebServiceUtil.getTrackingID(xmlStr), xmlStr, queueName);

			}
		} catch (IllegalStateException e) {
			log.error("Exception in reading message, The Session was already closed");
			log.debug("Exception in reading message: ", e);
			if (shouldRetry) {
				return readMessage(queueName, timeoutVal, clientId, false);
			}

		} catch (JMSException e) {
			log.error("Exception in reading message", e);
			if (shouldRetry) {
				return readMessage(queueName, timeoutVal, clientId, false);
			}
		} catch (UnsupportedEncodingException e) {
			log.error("Exception in reading message: ", e);
		} catch (NumberFormatException e) {
			log.error("Exception in reading message: ", e);
		} catch (Exception e) {
			log.debug("Exception in reading message, wrong tracking id {}", e.getMessage());
		}
		
		return xmlStr;
	}
	
	/*
	 * Helper method, for converting JMS message to the string, that should
	 * be send back to user.
	 * 
	 * @param m - JMS message
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws JMSException
	 */
	private String generateReceiveResponse(TextMessage m) throws UnsupportedEncodingException, JMSException {
		String encodedStr = URLEncoder.encode(m.getText().toString(), "UTF-8");
		StringBuilder xmlStr = new StringBuilder();
		
		xmlStr.append("<ReceiveMessageResponse><ReceiveMessageResult><Message><MessageId>")
        .append(m.getJMSMessageID())
        .append("</MessageId>")
        .append("<ReceiptHandle></ReceiptHandle><MD5OfBody>")
        .append(MBusWebServiceUtil.getMD5Hash(encodedStr))
        .append("</MD5OfBody><Body>")
        .append(encodedStr)
        .append("</Body>")
        .append("</Message></ReceiveMessageResult>")
        .append("<ResponseMetadata><RequestId> </RequestId>");
		xmlStr.append(generateCommonResponse(m));
		xmlStr.append("</ResponseMetadata></ReceiveMessageResponse>");

		return xmlStr.toString();
	}


	/**
	 * Overloaded method for deleting messages from the incoming queue.
	 * 
	 * @param queueName - make of the queue
	 * @param messageId - message identifier
	 * @param clientId - client identifier
	 * @return
	 */
	protected String deleteMessage(String queueName,String messageId, String clientId) {
		return deleteMessage(queueName, messageId, clientId, true);
	}
	
	/**
	 * Method for deleting messages from the JMS queue.
	 * 
	 * @param queueName - make of the queue
	 * @param messageId - message identifier
	 * @param clientId - client identifier
	 * @param shouldRetry - should delete operation be retried in case of exceptions.
	 * @return String - result of the operation 
	 */
	protected String deleteMessage(String queueName,String messageId, String clientId, boolean shouldRetry){

		final String success = 
				"<DeleteMessageResponse><ResponseMetadata><RequestStatus>SUCCESS</RequestStatus></ResponseMetadata></DeleteMessageResponse>";
		final String failure = 
				"<DeleteMessageResponse><ResponseMetadata><RequestStatus>FAILURE</RequestStatus></ResponseMetadata></DeleteMessageResponse>";
			
		String xmlStr = failure;
			
		if(queueName.equals("NULL_QUEUE")){
			return success;
		}

		try {
			 
			if (clientId == null) {
				clientId = "UNKNOWN CLIENT ID";
			}
			
			if ( jmsBridge.ackMessage(messageId) ) {
				log.debug("Message Deleted :  ClintID : {}  Message id : {}", clientId, messageId);
				xmlStr = success;
			} else {
                // CBW: with the broker plugin changes, is this dead code now?
				log.warn("Deletion request: Message not found with the message id");
				xmlStr = MBusWebServiceUtil.errorMessageBuilder(Constants.ERR_CODE_INTERNAL_ERROR);
			}
		} catch (JMSException e) {
			log.error("Exception in deleting message");
			if (shouldRetry) {
				return deleteMessage(queueName, messageId, clientId, false);
			}
		}

		return xmlStr;

	}
	
	
	/* it seem to be never used
	private String changeVisibility(String queueName, String timeoutval, String messageId) {
		return changeVisibility(queueName, timeoutval, messageId, true);
	}
	

	private String changeVisibility(String queueName, String timeoutval, String messageId, boolean shouldRetry) {
		
		int timeOutVal = 0;
						
		Connection connection;
		Session session;
		Destination destination;
		Message m = null;
		
		MessageConsumer consumer = null;
		final String success = 
			"<ChangeMessageVisibilityResponse><ResponseMetadata><RequestStatus>SUCCESS</RequestStatus></ResponseMetadata></ChangeMessageVisibilityResponse>";
		final String failure = 
			"<ChangeMessageVisibilityResponse><ResponseMetadata><RequestStatus>FAILURE</RequestStatus></ResponseMetadata></ChangeMessageVisibilityResponse>";

		String xmlStr = failure;
		
		try{
			timeOutVal = Integer.parseInt(timeoutval);
		}catch(java.lang.NumberFormatException e){
			log.debug("Exception in changeVisibility Number formate exception ::::" + e.getMessage());
			timeOutVal = 60;
		}
		try{			
			connection = obj.getConnection();
			session = connection.createSession(true, ActiveMQSession.CLIENT_ACKNOWLEDGE);
			destination = session.createQueue(queueName);					
			
			String queryString = "JMSMessageID =" +"'" + messageId+ "'"; 
			consumer = session.createConsumer(destination, queryString);
			
			int readTimeOut = Integer.parseInt(props.getProperty("xcal.mbus.readTimeOutValue"));
			m = consumer.receive(readTimeOut);
            consumer.close();
			
			if( m!= null){
				obj.saveSession(m.getJMSMessageID(), session);
				final  String messageID = m.getJMSMessageID();
				
				customTimer.createTimer(new Runnable(){
					@Override
					public void run() {
						obj.clearSession(messageID,"false");						
					}					
				}, timeOutVal, TimeUnit.SECONDS);
				xmlStr = success;
			}else{
				session.close();
			}
		}catch(JMSException e){
            if (shouldRetry && handleJMSException(e)) {
            	return changeVisibility(queueName, timeoutval, messageId, false);
            }
            log.error("Error in changeVisibility");
		}
		
		return xmlStr;
	}
	 */
	

}
