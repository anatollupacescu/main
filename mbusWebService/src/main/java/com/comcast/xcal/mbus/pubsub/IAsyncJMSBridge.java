package com.comcast.xcal.mbus.pubsub;

import javax.jms.JMSException;

import com.comcast.xcal.mbus.pubsub.cache.IAsyncConsumerCache;

/**
 * Interface, describing communication for publish\subscribe operations
 * on JMS.
 * 
 */
public interface IAsyncJMSBridge {
	
	/**
	 * Subscribe for the topic.
	 * 
	 * @param subscriptionContext - POJO, with all necessary parameters for topic communication. {@link SubscriptionContext}
	 * @return 
	 * @throws JMSException
	 */
    String subscribe(SubscriptionContext subscriptionContext) throws JMSException;

    /**
     * Unsubscribe from topic. Should release connections, remove listeners etc..
     * 
     * @param subscriptionContext - POJO, with all necessary parameters for topic communication. {@link SubscriptionContext}
     * @throws JMSException
     */
    void unsubscribe(SubscriptionContext subscriptionContext) throws JMSException;

    /**
     * Before starting to process subscribe operation, temporary topic should be created.
     * And user have to receive unique created topic name. Both, server and client should know
     * how to communicate for publish\subscribe, and it this should give ability for both of them
     * to know what destination is used for publish\subscribe.
     * 
     * @return
     * @throws JMSException
     */
    String prepareReplyTopic() throws JMSException;

    /**
     * Get consumer cache.
     * @return {@link IAsyncConsumerCache}
     */
    IAsyncConsumerCache getAsyncConsumerCache();
}
