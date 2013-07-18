package com.comcast.xcal.mbus.ptp;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

/**
 * Interface, describing communication mechanism with JMS broker. 
 * 
 */
public interface IJMSBridge {

	/**
	 * Method, describing "take" operation from JMS.
	 * 
	 * @param destinationType - TOPIC or QUEUE
	 * @param destinationName - name of the destination. 
	 * @return Message - JMS message
	 * @throws JMSException
	 */
    Message receiveMessage(String destinationType, String destinationName) throws JMSException;

	/**
	 * Overloaded Method, describing "take" operation from JMS with timeout specified.
	 * 
	 * @param destinationType - TOPIC or QUEUE
	 * @param destinationName - name of the destination. 
 	 * @param timeout - timeout in milliseconds to wait for message to appear in the queue
	 * @return Message - JMS message
	 * @throws JMSException
	 */
    Message receiveMessage(String destinationType, String destinationName, long timeout) throws JMSException;

    /**
     * 
	 * Overloaded Method, describing "take" operation from JMS with timeout and selector specified.
	 * 
	 * @param destinationType - TOPIC or QUEUE
	 * @param destinationName - name of the destination.
     * @param selector - selector for the messages to be consumed. (from JMS specification).
     * This is some conditions for messages, so they can be chosen by some principle.</br>
     * Example: (JMSCorrelationID="myID")
 	 * @param timeout - timeout in milliseconds.
	 * @return Message - JMS message
     * @throws JMSException
     */
    Message receiveMessage(String destinationType, String destinationName, String selector, long timeout) throws JMSException;

    /**
     * Method, describing put operation on JMS queue or publish for topic.
     * 
	 * @param destinationType - TOPIC or QUEUE
	 * @param destinationName - name of the destination.
     * @param message - string representation of message should be send.
     * @return TextMessage - constructed message should be returned, so it can be send to the client, as a result of operation
     * @throws JMSException
     */
    TextMessage sendTextMessage(String destinationType, String destinationName, String message) throws JMSException;

    /**
     * Overloaded method, describing put operation on JMS queue or publish for topic, with correlation id identifier.
     * 
	 * @param destinationType - TOPIC or QUEUE
	 * @param destinationName - name of the destination.
     * @param correlationId - correlation id - identifier of message.
     * @param message - string representation of message should be send.
     * @return TextMessage - constructed message should be returned, so it can be send to the sender, as a result of operation
     * @throws JMSException
     */
    TextMessage sendTextMessage(String destinationType, String destinationName, String correlationId, String message) throws JMSException;

    /**
     * Overloaded method, describing put operation on JMS queue or publish for topic, with correlation id identifier,
     * and replyTo.
     *  
	 * @param destinationType - TOPIC or QUEUE
	 * @param destinationName - name of the destination.
     * @param correlationId - identifier of message.
     * @param replyTo - identifier of message sender
     * @param message - string representation of message should be send.
     * @return TextMessage - constructed message should be returned, so it can be send to the user, as a result of operation
     * @throws JMSException
     */
    TextMessage sendTextMessage(String destinationType, String destinationName, String correlationId, String replyTo, String message) throws JMSException;
    
    /**
     * Overloaded method, describing put operation on JMS queue or publish for topic, with correlation id identifier,
     * and replyTo and JMS message priority.
     *  
	 * @param destinationType - TOPIC or QUEUE
	 * @param destinationName - name of the destination.
     * @param correlationId - identifier of message.
     * @param replyTo - identifier of message sender
     * @param priority - JMS priority for message. (1-9)
     * @param message - string representation of message should be send.
     * @return TextMessage - constructed message should be returned, so it can be send to the sender, as a result of operation
     * @throws JMSException
     */
    TextMessage sendTextMessage(String destinationType, String destinationName, String correlationId, String replyTo, Integer priority, String message) throws JMSException;
    
    /**
     * Acknowledgment of message. After message is received and processed,
     * message should be acknowledged.
     * 
     * @param messageId
     * @return boolean
     * @throws JMSException
     */
    boolean ackMessage(String messageId) throws JMSException;

    /**
     * Method, should be triggered in order to reap expired message. 
     * You do not need to handle message reaping, it is automatically executed 
     * by web service. 
     */
    void reapExpiredMessages();

    /**
     * Overloaded method, describing put operation on JMS queue or publish for topic, with correlation id identifier,
     * and replyTo and JMS priority.
     * 
	 * @param destinationType - TOPIC or QUEUE
	 * @param destinationName - name of the destination.
     * @param correlationId - identifier of message.
     * @param replyTo - identifier of message sender
     * @param priority - JMS priority for message. (1-9)
     * @param message - string representation of message should be send.
     * @param nonPersistent - message persistence (should it stored to db or not)
     * @return TextMessage - constructed message should be returned, so it can be send to the sender, as a result of operation
     * @throws JMSException
     */
    TextMessage sendTextMessage(String destinationType, String destinationName, String correlationId, String replyTo, Integer priority, String message, Boolean nonPersistent) throws JMSException;
}
