package com.comcast.xcal.mbus.pubsub.cache;

import javax.jms.MessageConsumer;
import javax.jms.Session;

/**
 * Class, describing session and the related consumer.</br>
 * Is used as value in key\value pair of {@link AsyncConsumerCache} </br>
 * Simple POJO.
 * 
 */
public class AsyncConsumerCacheValue {

    private final Session session;
    private final MessageConsumer consumer;

    public AsyncConsumerCacheValue(Session session, MessageConsumer consumer) {
        this.session = session;
        this.consumer = consumer;
    }

    public MessageConsumer getConsumer() {
        return consumer;
    }

    public Session getSession() {
        return session;
    }

	@Override
	public String toString() {
		return "AsyncConsumerCacheValue [session=" + session + ", consumer=" + consumer + "]";
	}
    
    
}
