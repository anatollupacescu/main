package com.comcast.xcal.mbus.pubsub;

import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.comcast.xcal.mbus.pubsub.cache.AsyncConsumerCacheKey;
import com.comcast.xcal.mbus.pubsub.cache.AsyncConsumerCacheValue;
import com.comcast.xcal.mbus.pubsub.cache.IAsyncConsumerCache;

/**
 * Basic implementation of the {@link IAsyncJMSBridge}
 * 
 */
@Component
public class AsyncJMSBridge implements IAsyncJMSBridge {

	private static Logger LOG = LoggerFactory.getLogger("mbusWebServiceLogger");

	private final PooledConnectionFactory subscriptionConnectionFactory;

	private final IAsyncConsumerCache asyncConsumerCache;

	@Inject
	public AsyncJMSBridge(PooledConnectionFactory connectionFactory, IAsyncConsumerCache asyncConsumerCache) {
		this.subscriptionConnectionFactory = connectionFactory;
		this.asyncConsumerCache = asyncConsumerCache;
	}

	/**
	 * Subscription mechanism.
	 * First the connection and session are created.
	 * Afterwards, the topic is created based on the destination name in the
	 * subscriptionContext. The topic name is usually the
	 * 'serviceName/correlationId'. The
	 * correlationId might be null, especially for the pub/sub method.
	 * Then, the consumer and the subscription context are stored in a cache,
	 * with the {@link MessageListener} from the subscription context
	 * being added to the consumer.
	 * 
	 * @return - the topic name of the subscription
	 */
	@Override
	public String subscribe(SubscriptionContext subscriptionContext) throws JMSException {
		LOG.debug("In subscribe({})", subscriptionContext);
		LOG.debug("subscriptionConnectionFactory.getExpiryTimeout(): " + subscriptionConnectionFactory.getExpiryTimeout());
		LOG.debug("subscriptionConnectionFactory.getIdleTimeout(): " + subscriptionConnectionFactory.getIdleTimeout());
		
		subscriptionConnectionFactory.setExpiryTimeout(Integer.MAX_VALUE);
		subscriptionConnectionFactory.setIdleTimeout(Integer.MAX_VALUE);
	

		final Connection connection = subscriptionConnectionFactory.createConnection();
		
		LOG.debug("updated subscriptionConnectionFactory.getExpiryTimeout(): " + subscriptionConnectionFactory.getExpiryTimeout());
		LOG.debug("updated subscriptionConnectionFactory.getIdleTimeout(): " + subscriptionConnectionFactory.getIdleTimeout());
	
		connection.setExceptionListener(new ExceptionListener() {
			
			@Override
			public void onException(JMSException exception) {
				LOG.error("---JMS Connection exception", exception);
			}
		});
		
		connection.start();

		final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		
		LOG.debug(" --- Topic name in subscribe: " + subscriptionContext.getDestinationName());
		Topic destination = session.createTopic(subscriptionContext.getDestinationName());
		String selector = getSelectorForCorrelationId(subscriptionContext.getCorrelationId());
		
		LOG.debug(" --- Creating a consumer for the topic with the name of {} and selector of {}: " , destination.getTopicName(), selector);
		final MessageConsumer consumer = session.createConsumer(destination);
		
		AsyncConsumerCacheKey consumerCacheKey = new AsyncConsumerCacheKey(subscriptionContext);
		AsyncConsumerCacheValue consumerCacheValue = new AsyncConsumerCacheValue(session, consumer);
		asyncConsumerCache.putConsumer(consumerCacheKey, consumerCacheValue);
		
		LOG.debug("Setting the message listener in the consumer: " + subscriptionContext);
		consumer.setMessageListener(subscriptionContext.getMessageListener());

		return destination.getTopicName();

	}

	/**
	 * Unsubscribing from the topic.
	 * TODO: we're not killing the broadcast
	 */
	@Override
	public void unsubscribe(SubscriptionContext subscriptionContext) throws JMSException {

		LOG.debug("Unsubscribe called for {} / {}", subscriptionContext.getDestinationName(), subscriptionContext.getCorrelationId());

		AsyncConsumerCacheKey consumerCacheKey = new AsyncConsumerCacheKey(subscriptionContext);
		if (asyncConsumerCache.consumerExists(consumerCacheKey)) {
			AsyncConsumerCacheValue consumerCacheValue = asyncConsumerCache.flushConsumer(consumerCacheKey);
			Session session = consumerCacheValue.getSession();
			MessageConsumer consumer = consumerCacheValue.getConsumer();
			if (consumer != null) {
				LOG.debug("Closing consumer for: {} / {}", subscriptionContext.getDestinationName(), subscriptionContext.getCorrelationId());
				consumer.close();
				session.close();
			}
		}
	}

	/**
	 * Realization of prepare topic.
	 * 
	 * Actions to perform:</br>
	 * - grab connection from subscriptionConnectionFactory</br>
	 * - create session</br>
	 * - create destination</br>
	 * - create temporary topic</br>
	 * - return topic name
	 * 
	 */
	@Override
	public String prepareReplyTopic() throws JMSException {
		Connection connection = subscriptionConnectionFactory.createConnection();
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Topic destination = session.createTemporaryTopic();
		String replyTopicName = destination.getTopicName();
		session.close();
		return replyTopicName;
	}

	@Override
	public IAsyncConsumerCache getAsyncConsumerCache() {
		return asyncConsumerCache;
	}

	/**
	 * Helper method, for constructing selector with JMSCorrelationID
	 * 
	 * @param correlationId
	 * @return String with correlationId "filter"
	 */
	private String getSelectorForCorrelationId(String correlationId) {
		String selector = null;
		if (correlationId != null) {
			selector = String.format("JMSCorrelationID = '%s'", correlationId);
		}
		// return selector;
		return null;
	}

}
