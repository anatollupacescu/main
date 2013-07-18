package com.comcast.xcal.mbus.pubsub;

import javax.jms.MessageListener;

/**
 * Builder pattern for SubscriptionContextBuilder. Some of values 
 * can be null and not passed. Builder pattern helps to handle object 
 * creation with lots of parameter.
 *
 */
public class SubscriptionContextBuilder {
    private String destinationName;
    private String correlationId;
    private String readTimeOutVal;
    private Boolean temporary;
    private MessageListener messageListener;
    private String sessionId;

    public SubscriptionContextBuilder setDestinationName(String destinationName) {
        this.destinationName = destinationName;
        return this;
    }

    public SubscriptionContextBuilder setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
        return this;
    }

    public SubscriptionContextBuilder setReadTimeOutVal(String readTimeOutVal) {
        this.readTimeOutVal = readTimeOutVal;
        return this;
    }

    public SubscriptionContextBuilder setTemporary(Boolean temporary) {
        this.temporary = temporary;
        return this;
    }

    public SubscriptionContextBuilder setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
        return this;
    }

    public SubscriptionContextBuilder setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public SubscriptionContext createSubscriptionContext() {
        return new SubscriptionContext(destinationName, correlationId, readTimeOutVal, temporary, messageListener, sessionId);
    }
}