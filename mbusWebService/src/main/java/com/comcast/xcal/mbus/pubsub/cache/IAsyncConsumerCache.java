package com.comcast.xcal.mbus.pubsub.cache;


/**
 * Consumer cache interface for async operation on the JMS resources.
 * 
 */
public interface IAsyncConsumerCache {

	/**
	 * Method, describing putting consumer into the cache
	 * 
	 * @param consumerCacheKey {@link AsyncConsumerCacheKey} - describes, where to store values. KEY
	 * @param consumer {@link AsyncConsumerCacheValue} - describes data, should be stored. VALUE
	 */
    void putConsumer(AsyncConsumerCacheKey consumerCacheKey, AsyncConsumerCacheValue consumer);

    /**
     * Method for retrieving consumer from the consumer cache by key.
     * 
     * @param consumerCacheKey {@link AsyncConsumerCacheKey}
     * @return AsyncConsumerCacheValue
     */
    AsyncConsumerCacheValue getConsumer(AsyncConsumerCacheKey consumerCacheKey);

    /**
     * Method, describing existence of consumer in the cache.
     * 
     * @param consumerCacheKey {@link AsyncConsumerCacheKey}
     * @return boolean - true if exists
     */
    boolean consumerExists(AsyncConsumerCacheKey consumerCacheKey);

    /**
     * Flush consumer. Read and remove from the map.
     * 
     * @param {@link AsyncConsumerCacheKey}
     * @return {@link AsyncConsumerCacheKey}
     */
    AsyncConsumerCacheValue flushConsumer(AsyncConsumerCacheKey consumerCacheKey);
}
