package com.comcast.xcal.mbus.pubsub.cache;

import com.comcast.xcal.mbus.pubsub.SubscriptionContext;

/**
 * 
 * Class, responsible for keeping  session and the related consumer.
 * Is representation of key for the map {@link IAsyncConsumerCache}.
 * Simple POJO.
 *
 */
public class AsyncConsumerCacheKey {

    private final String destinationName;
    private final String correlationId;
    private final String sessionId;

    /* create a key straight from broadcaster */
    public AsyncConsumerCacheKey(String sessionId) {
        this.destinationName = null;
        this.correlationId = null;
        this.sessionId = sessionId;
    }

    public AsyncConsumerCacheKey(String destinationName, String correlationId, String sessionId) {
        this.destinationName = destinationName;
        this.correlationId = correlationId;
        this.sessionId = sessionId;
    }

    public AsyncConsumerCacheKey(SubscriptionContext subscriptionContext) {
        this.destinationName = subscriptionContext.getDestinationName();
        this.correlationId = subscriptionContext.getCorrelationId();
        this.sessionId = subscriptionContext.getSessionId();
    }

    public String getDestinationName() {
        return destinationName;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
