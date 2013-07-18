package com.comcast.xcal.mbus.pubsub;

import javax.jms.MessageListener;

/**
 * Subscription context. Is just a Plain old java object,
 * which is used for broadcast operation.</br>
 * Contains values, that should be compared\retrieved etc.
 *
 */
public class SubscriptionContext {

    private final String destinationName;
    private final String correlationId;
    private final String readTimeOutVal;
    private final Boolean temporary;
    private final MessageListener messageListener;
    private final String sessionId;

    public SubscriptionContext(String destinationName,
                               String correlationId,
                               String readTimeOutVal,
                               Boolean temporary,
                               MessageListener messageListener,
                               String sessionId) {
        this.destinationName = destinationName;
        this.correlationId = correlationId;
        this.readTimeOutVal = readTimeOutVal;
        this.temporary = temporary;
        this.messageListener = messageListener;
        this.sessionId = sessionId;
    }

    public SubscriptionContext(String destinationName, String correlationId, String sessionId) {
        this.destinationName = destinationName;
        this.correlationId = correlationId;
        this.sessionId = sessionId;
        this.readTimeOutVal = null;
        this.temporary = null;
        this.messageListener = null;
    }

    /**
     * Return JMS destination name
     * 
     * @return String - name of queue to write\read.
     */
    public String getDestinationName() {
        return destinationName;
    }

    /**
     * Return Correlation id from message.
     * 
     * @return String - unique identifier of the message.
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * Return timeout for reading from the queue.
     * 
     * @return String - timeout in milliseconds
     */
    public String getReadTimeOutVal() {
        return readTimeOutVal;
    }

    /**
     * Return "if this topic temporary or not".
     * @return Boolean
     */
    public Boolean getTemporary() {
        return temporary;
    }

    /**
     * Return jms listener.
     * @return MessageListener
     */
    public MessageListener getMessageListener() {
        return messageListener;
    }

    /**
     * Return HTTP session identifier
     * @return String - session id
     */
    public String getSessionId() {
        return sessionId;
    }

	@Override
	public String toString() {
		return "SubscriptionContext [destinationName=" + destinationName + ", correlationId=" + correlationId + ", readTimeOutVal=" + readTimeOutVal
				+ ", temporary=" + temporary + ", messageListener=" + messageListener + ", sessionId=" + sessionId + "]";
	}
    
    
}
