package com.comcast.xcal.mbus.ptp;

import java.io.IOException;
import java.util.Map;

import com.rabbitmq.client.GetResponse;


public interface IAMQPBridge {
	
    GetResponse receiveMessage(String destinationType, String destinationName) throws IOException;
    GetResponse receiveMessage(String destinationType, String destinationName, long timeout) throws IOException;
    GetResponse receiveMessage(String destinationType, String destinationName, String selector, long timeout) throws IOException;

    Map<String,Object> sendTextMessage(String destinationType, String destinationName, String message) throws IOException;
    Map<String,Object> sendTextMessage(String destinationType, String destinationName, String correlationId, String message) throws IOException;
    Map<String,Object> sendTextMessage(String destinationType, String destinationName, String correlationId, String replyTo, String message) throws IOException;
    Map<String,Object> sendTextMessage(String destinationType, String destinationName, String correlationId, String replyTo, Integer priority, String message) throws IOException;
    Map<String,Object> sendTextMessage(String destinationType, String destinationName, String correlationId, String replyTo, Integer priority, String message, Boolean nonPersistent) throws IOException;
  
    boolean ackMessage(String messageId);
    void reapExpiredMessages();
   

}
