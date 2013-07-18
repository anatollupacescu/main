package com.comcast.xcal.mbus.ptp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.comcast.xcal.mbus.config.IMBusWebServiceProperties;
import com.comcast.xcal.mbus.util.Reaper;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.QueueingConsumer;


public class AMQPBridge implements IAMQPBridge {
	
	public static final String ID_TO_DELETE = "idToDelete";
	

    private static Logger log = LoggerFactory.getLogger("mbusWebServiceLogger");
    private static final String DIRECT_EXCHANGE = "DIRECT_EXCHANGE";
    private static final String DIRECT_ROUTING_KEY = "DIRECT_ROUTING_KEY";
    private static final String TOPIC_EXCHANGE = "TOPIC_EXCHANGE";
    
    public Properties props;

    private String brokerURL;


    private ConnectionFactory connectionFactory = null;
    private Connection connection = null;
    private QueueingConsumer consumer = null;
   
    String inflightDestinationName = null;
    Integer producerSendTimeout;

    boolean bridgeInitialized = false;
    
    private long readTimeout = 0L;

	public AMQPBridge(IMBusWebServiceProperties mbusWebServiceProperties,
            ConnectionFactory connectionFactory)  throws IOException {

		this.props = mbusWebServiceProperties.getProperties();

		if (bridgeInitialized)
			return;
		
		try{
			if (connectionFactory == null) {
				log.error("IS THIS A UNIT TEST? ActiveMQConnectionFactory not injected properly");
				log.warn("Using development connection factory...");
				connectionFactory = new ConnectionFactory();
				connectionFactory.setUsername(props.getProperty("xcal.mbus.rabbitmq.username"));
				connectionFactory.setPassword(props.getProperty("xcal.mbus.rabbitmq.password"));
				connectionFactory.setVirtualHost("/");
				connectionFactory.setHost(props.getProperty("xcal.mbus.rabbitmq.host"));
				connectionFactory.setPort(Integer.parseInt(props.getProperty("xcal.mbus.rabbitmq.port")));			
			}
			
			if(connection == null){
				connection = connectionFactory.newConnection();
			}
			readTimeout = Long.parseLong(props.getProperty("xcal.mbus.message.readTimeout", "20000"));
			bridgeInitialized = true;
			
			log.debug("Initializing JMSBridge...");
		}catch (Exception e) {
			stop();
		}
	}
	
	private void stop() {
		try {
			connection.close();
			//TODO Maybe should be channel closed to
		} catch (Exception e) {
			log.error("Error cleaning up resources: {}", e.getMessage());
		} finally {
			bridgeInitialized = false;
		}

	}
	
	 /** put a message to the MY_DIRECT_EXCHANGE exchange with rounting key = 'MY_DIRECT_ROUNTING_KEY '*/
	 private void insertMessageToQueue(String destinationName,String exchangeName,String routingKey, String message) throws IOException {
		  
		  Channel channel = connection.createChannel();
		  channel.exchangeDeclare(exchangeName, "direct", false);
		  channel.queueDeclare(destinationName, false, true, false, null);
		  channel.queueBind(destinationName, exchangeName, routingKey);
		  
		  byte[] input = message.getBytes();
		  channel.basicPublish(exchangeName, routingKey, null, input);
		  channel.close();  
	 }
	 
	 
	 
	 
	 private void insertMessageToTopic(String destinationName,String routingKey,String exchangeTopic,String message) throws IOException {
		  Channel channel = connection.createChannel();
		  channel.exchangeDeclare(exchangeTopic, "topic", false);
		  channel.queueDeclare(destinationName, false, true, false, null);
		  channel.queueBind(destinationName, exchangeTopic, routingKey);
		  
		  byte [] input = message.getBytes();
		  channel.basicPublish("MY_TOPIC_EXCHANGE", "TEST.ROUNTING_KEY", null, input);
		  channel.close();
	 }

	@Override
	public GetResponse receiveMessage(String destinationType,
			String destinationName) throws IOException {
		
		return receiveMessage(destinationType, destinationName, null, readTimeout);
	}

	@Override
	public GetResponse receiveMessage(String destinationType,
			String destinationName, long timeout) throws IOException {
		
		 return receiveMessage(destinationType, destinationName, null, timeout);
	}

	@Override
	public GetResponse receiveMessage(String destinationType,
			String destinationName, String selector, long timeout)
			throws IOException {
			 
			   Channel channel = connection.createChannel();
			   GetResponse getResponse = channel.basicGet(destinationName, false);
			  
		return getResponse;
	}

	@Override
	public Map<String, Object> sendTextMessage(String destinationType,
			String destinationName, String message) throws IOException{
		
		 return sendTextMessage(destinationType, destinationName, null, null, message);
	
	}

	@Override
	public Map<String, Object> sendTextMessage(String destinationType,
			String destinationName, String correlationId, String message) throws IOException{
		
		 return sendTextMessage(destinationType, destinationName, correlationId, null, message);
	}

	@Override
	public Map<String, Object> sendTextMessage(String destinationType,
			String destinationName, String correlationId, String replyTo,
			String message) throws IOException{
		
		return sendTextMessage(destinationType, destinationName, correlationId, replyTo, null, message);
	}

	@Override
	public Map<String, Object> sendTextMessage(String destinationType,
			String destinationName, String correlationId, String replyTo,
			Integer priority, String message) throws IOException{
		
		 return sendTextMessage(destinationType, destinationName, correlationId, replyTo, priority, message, false);
	}

	@Override
	public Map<String, Object> sendTextMessage(String destinationType,
			String destinationName, String correlationId, String replyTo,
			Integer priority, String message, Boolean nonPersistent)
			throws IOException {

		Channel channel = connection.createChannel();
		Map<String, Object> returnMap = new HashMap<String, Object>();
		int deliveryMode = (nonPersistent) ? 1 : 2;
		
		BasicProperties properties = new BasicProperties.Builder()
				.messageId(UUID.randomUUID().toString())
				.correlationId(UUID.randomUUID().toString())
				.deliveryMode(deliveryMode).replyTo(replyTo).priority(priority)
				.build();
		returnMap.put("correlationId", properties.getCorrelationId());
		returnMap.put("messageId", properties.getMessageId());
		
		if ("QUEUE".equalsIgnoreCase(destinationType)) {

			channel.exchangeDeclare(DIRECT_EXCHANGE, "direct", false);
			channel.queueDeclare(destinationName, false, true, false, null);
			channel.queueBind(destinationName, DIRECT_EXCHANGE,
					DIRECT_ROUTING_KEY);	

			channel.basicPublish(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY,
					properties, message.getBytes());
			channel.close();

		} else if ("TOPIC".equalsIgnoreCase(destinationType)) {
			returnMap = new HashMap<String, Object>();

			channel.exchangeDeclare(TOPIC_EXCHANGE, "topic", false);
			channel.queueDeclare(destinationName, false, true, false, null);
			channel.queueBind(destinationName, TOPIC_EXCHANGE, "*.ROUNTING_KEY");

			channel.basicPublish(TOPIC_EXCHANGE, destinationName, properties, message.getBytes());
			channel.close();
		}

		return returnMap;
	}

	@Override
	public boolean ackMessage(String messageId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reapExpiredMessages() {
		// TODO Auto-generated method stub
		
	}

	
	
//	public void receiveMessage(String destinationType,
//			String destinationName, String selector, long timeout) {
//			try {
//			   /*
//			   System.out.println("inserting");
//			   //TODO this call should be removed of course
//			   insertSampleMessageToQueue();
//			   Channel channel = connection.createChannel();
//			   
//			   GetResponse getResponse = channel.basicGet("MY_DIRECT_QUEUE", false);
//			   String response = new String(getResponse.getBody());
//			   System.out.println(response);
//			   */
//			   insertSampleMessageToTopic();
//			   Channel channel = connection.createChannel();
//			   GetResponse getResponse = channel.basicGet("MY_TOPIC_QUEUE", false);
//			   String response = new String(getResponse.getBody());
//			   System.out.println("correlationID= " + getResponse.getProps().getCorrelationId());
//			   System.out.println("messageID= " + getResponse.getProps().getMessageId());
//			   System.out.println(response);
//			  } catch (IOException e) {
//			   e.printStackTrace();
//			  }
//		
//		
//	}

	

}
