package com.comcast.xcal.mbus.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Audit logger implementation for message bus webservice.
 * In order to have separate and easy to use\implement audit logging,
 * this class was created. It describes to have only 2 static methods:
 * read and write, and logback.xml config is set to have separate files 
 * for this logs.
 *
 */
public class AuditLoggingUtil {
	final static Logger log = LoggerFactory.getLogger(AuditLoggingUtil.class);

	/**
	 * Logging received message
	 * 
	 * @param trackingID - tracking id, taken from the message.
	 * @param message - actual body of message
	 * @param queueName - string, describing from what endpoint message was consumed.
	 */
	public static void logReceived(String trackingID, String message, String queueName){
		log.info("Component [mbusWebService] trackingid [{}] message [{}] action [{}] from [{}]", new Object[]{trackingID, message, "received", queueName});
	}
	
	/**
	 * Logging send message.
	 * 
	 * @param trackingID - tracking id, taken from the message.
	 * @param message - actual body of message
	 * @param queueName - string, describing what endpoint gave the message
	 */
	public static void logSend(String trackingID, String message, String queueName){
		log.info("Component [mbusWebService] trackingid [{}] message [{}] action [{}] to [{}].", new Object[]{trackingID, message, "send", queueName});
	}
}
