package com.comcast.xcal.mbus.pubsub.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Consumer cache. Implementation of the IAsyncConsumerCache.
 * Describe operation on the Consumer cache map.
 *
 */
@Component
public class AsyncConsumerCache implements IAsyncConsumerCache {
	private static Logger LOG = LoggerFactory.getLogger("AsyncConsumerCache");
    final Map<String, AsyncConsumerCacheValue> consumerMap = new ConcurrentHashMap<String, AsyncConsumerCacheValue>();

    public AsyncConsumerCache() {
    }

    private Map<String, AsyncConsumerCacheValue> getConsumerMap() {
        return consumerMap;
    }

	/**
	 * Method, describing putting consumer into the cache
	 * 
	 * @param consumerCacheKey {@link AsyncConsumerCacheKey} - describes, where to store values. KEY
	 * @param consumer {@link AsyncConsumerCacheValue} - describes data, should be stored. VALUE
	 */
    @Override
    public void putConsumer(AsyncConsumerCacheKey consumerCacheKey, AsyncConsumerCacheValue consumer) {
    	LOG.debug("Adding a consumer to cache. Current size is {} ", consumerMap.size());
        getConsumerMap().put(consumerCacheKey.getSessionId(), consumer);
    }

    /**
     * Method for retrieving consumer from the consumer cache by key.
     * 
     * @param consumerCacheKey {@link AsyncConsumerCacheKey}
     * @return AsyncConsumerCacheValue
     */
    @Override
    public AsyncConsumerCacheValue getConsumer(AsyncConsumerCacheKey consumerCacheKey) {
        return getConsumerMap().get(consumerCacheKey.getSessionId());
    }

    /**
     * Method, describing existence of consumer in the cache.
     * 
     * @param consumerCacheKey {@link AsyncConsumerCacheKey}
     * @return boolean - true if exists
     */
    @Override
    public boolean consumerExists(AsyncConsumerCacheKey consumerCacheKey) {
        return getConsumerMap().containsKey(consumerCacheKey.getSessionId());
    }

    /**
     * Flush consumer. Read and remove from the map.
     * 
     * @param {@link AsyncConsumerCacheKey}
     * @return {@link AsyncConsumerCacheKey}
     */
    @Override
    public AsyncConsumerCacheValue flushConsumer(AsyncConsumerCacheKey consumerCacheKey) {
    	LOG.debug("Removing a consumer from cache. Current size is {} ", consumerMap.size());
        return getConsumerMap().remove(consumerCacheKey.getSessionId());
    }
}
