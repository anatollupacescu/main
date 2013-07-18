package com.comcast.xcal.mbus.pubsub.listener;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.cpr.BroadcasterListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.comcast.xcal.mbus.pubsub.cache.AsyncConsumerCacheKey;
import com.comcast.xcal.mbus.pubsub.cache.IAsyncConsumerCache;

/**
 * {@link BroadcasterListener} implementation. </br>
 * We are interesting only on one event
 * onComplete to be able to remove and clean listener.
 *
 */
public class MBusBroadcasterListener implements BroadcasterListener {
	private static final Logger LOG = LoggerFactory.getLogger("MBusBroadcasterListener");
	private BroadcasterFactory broadcasterFactory;
    private IAsyncConsumerCache asyncConsumerCache;

    /**
     * Constructor.
     * 
     * @param broadcasterFactory - {@link BroadcasterFactory}
     * @param asyncConsumerCache - {@link IAsyncConsumerCache}
     */
	public MBusBroadcasterListener(BroadcasterFactory broadcasterFactory, IAsyncConsumerCache asyncConsumerCache){
		this.broadcasterFactory = broadcasterFactory;
        this.asyncConsumerCache = asyncConsumerCache;
		
	}

	@Override
	public void onPostCreate(Broadcaster broadcaster) {
		
	}
	
	//or put in on preDestroy!! and we can have nice cleanup, but!! it has to be triggered from client, if someone
	//can do this please make changes here.

	/**
	 * onComplete callback, broadcaster is destroyed\removed from the broadcaster factory
	 * and removed from consumer cache.
	 */
	@Override
	public void onComplete(Broadcaster broadcaster) {

	}

	@Override
	public void onPreDestroy(Broadcaster broadcaster) {
		LOG.debug("onPreDestroy - destroying the broadcaster");
        AsyncConsumerCacheKey consumerCacheKey = new AsyncConsumerCacheKey(null, null, broadcaster.getID());
        asyncConsumerCache.flushConsumer(consumerCacheKey);
//		broadcaster.destroy();
		broadcaster.releaseExternalResources(); //do we need it?
		broadcasterFactory.remove(broadcaster.getID()); // do we need it?
	}

	@Override
	public void onAddAtmosphereResource(Broadcaster arg0,
			AtmosphereResource arg1) {
		LOG.debug("onAddAtmosphereResource ");

		
	}

	@Override
	public void onRemoveAtmosphereResource(Broadcaster arg0,
			AtmosphereResource arg1) {
		LOG.debug("onRemoveAtmosphereResource ");
		
	}

}
