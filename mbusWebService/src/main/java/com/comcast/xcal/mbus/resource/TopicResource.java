package com.comcast.xcal.mbus.resource;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

import org.atmosphere.annotation.Suspend;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.jersey.JerseyBroadcaster;
import org.atmosphere.jersey.SuspendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comcast.xcal.mbus.config.IMBusWebServiceProperties;
import com.comcast.xcal.mbus.ptp.IJMSBridge;
import com.comcast.xcal.mbus.pubsub.IAsyncJMSBridge;
import com.comcast.xcal.mbus.pubsub.SubscriptionContext;
import com.comcast.xcal.mbus.pubsub.SubscriptionContextBuilder;
import com.comcast.xcal.mbus.pubsub.listener.MBusBroadcasterListener;
import com.comcast.xcal.mbus.pubsub.listener.MBusJMSMessageListener;
import com.sun.jersey.spi.container.servlet.PerSession;

/**
 * Class, to be mapped for working with JMS topics.
 * Can be used for 2 situations:</br>
 * 		- subscribe for messages.</br>
 * 		- publish messages to topic.
 *
 */
@Path("/topic/{destinationName}")
@Component
@PerSession
@Scope("request")
public class TopicResource {

    private static Logger LOG = LoggerFactory.getLogger("mbusWebServiceLogger");

    private static final String PREPARE_TEMPORARY_TOPIC_RESOURCE_NAME = "TEMPORARY";
    private static final String PREPARE_LONG_LIVED_TOPIC_RESOURCE_NAME = "LONG_LIVED";

    @Inject
    IMBusWebServiceProperties mbusWebServiceProperties;

    @Inject
    IJMSBridge jmsBridge;

    @Inject
    IAsyncJMSBridge asyncJMSBridge;

    @PathParam("destinationName")
    String destinationName;

    @Context
    private BroadcasterFactory broadcasterFactory;

    @Deprecated
    @PUT
    public String prepareTopic(
            @Context HttpServletRequest request
    ) {


        final String sessionId = request.getSession(true).getId();
        LOG.debug("Created sessionId {} for Topic", sessionId);

        if (destinationName.equals(PREPARE_TEMPORARY_TOPIC_RESOURCE_NAME)) {
            String physicalDestinationName = null;
    
            try {
                physicalDestinationName = asyncJMSBridge.prepareReplyTopic();
            } catch (JMSException e) {
                LOG.error("Problem preparing Temporary Topic", e);
            }
    
            LOG.debug("Created physicalDestinationName {} for Temporary Topic", physicalDestinationName);
    
            return physicalDestinationName;

        } else if (destinationName.equals(PREPARE_LONG_LIVED_TOPIC_RESOURCE_NAME)) {

            /* just return immediately - client wants a JSESSIONID cookie */
            return "SUCCESS";

        }
        
        throw new WebApplicationException(new IllegalArgumentException("PUT only supported for [" +
                PREPARE_TEMPORARY_TOPIC_RESOURCE_NAME + ", " + PREPARE_LONG_LIVED_TOPIC_RESOURCE_NAME +
                "]"), 405);

    }

    /**
     * Method for unsubscribing from the topic, based on correlationID and path.
     * 
     * @param correlationId - unique identifier
     * @param request - HttpServletRequest
     * @return FAILURE/SUCCESS
     */
    @DELETE
    public String unsubscribe(
            @HeaderParam("CorrelationId") final String correlationId,
            @Context final HttpServletRequest request
    ) {
        try {

            final String sessionId = request.getSession().getId();

            SubscriptionContext context = new SubscriptionContextBuilder()
                    .setDestinationName(destinationName)
                    .setCorrelationId(correlationId)
                    .setSessionId(sessionId)
                    .createSubscriptionContext();

            asyncJMSBridge.unsubscribe(context);

        } catch (JMSException e) {
            LOG.error("Problem unsubscribing from " + destinationName, e);
            return "FAILURE";
        }
        return "SUCCESS";
    }

    /**
     * Subscribe for the JMS topic. All preparation with listeners\broadcaster
     * is done with this GET call.
     * 
     * @param action - should be subscribe
     * @param version - not used
     * @param protocol - not used
     * @param endpoint - not used
     * @param readTimeOutVal - not used
     * @param isTemporary - does this subscribe relates to temporary or persistent JMS connection
     * @param correlationId - correlationId of message
     * @param request - HttpServletRequest
     * @return {@link SuspendResponse}
     */
    @GET
    @Suspend
    public SuspendResponse<String> subscribe(
            @QueryParam("Action") final String action,
            @QueryParam("version") final String version,
            @QueryParam("Protocol") final String protocol,
            @QueryParam("Endpoint") final String endpoint,
            @QueryParam("ReadTimeOutValue") final String readTimeOutVal,
            @DefaultValue("false") @QueryParam("isTemporary") final Boolean isTemporary,
            @HeaderParam("CorrelationId") final String correlationId,
            @Context final HttpServletRequest request
    ) {
    	LOG.debug("--- uponSubscribe --- corId: " + correlationId + " readTimeOut: " + readTimeOutVal + " isTemp: " + isTemporary + " destinationName: "  + destinationName);
        try {


            if (action != null && action.equals("Subscribe")) {
                /* subscribe to topic via destinationName and suspend the request */
                final String sessionId = request.getSession().getId();
                
                String topicNameMessage = null;
               	if (isTemporary 
               			&& "temporary".equalsIgnoreCase(destinationName)) { // for backward compatibility - do this only when connector specified this topic name
                    try {
                    	destinationName = asyncJMSBridge.prepareReplyTopic();
                        topicNameMessage = "<topic>" + destinationName + "</topic>";
                    } catch (JMSException e) {
                        LOG.error("Problem preparing Temporary Topic", e);
                    }
            
                    LOG.debug("Created physicalDestinationName {} for Temporary Topic", topicNameMessage);
               	}
               	
                MBusBroadcasterListener broadcasterListener = new MBusBroadcasterListener(broadcasterFactory, asyncJMSBridge.getAsyncConsumerCache());
                Broadcaster broadcaster = getBroadcaster(getID(destinationName, correlationId));
                MBusJMSMessageListener messageListener = new MBusJMSMessageListener(broadcaster, broadcasterListener, destinationName, correlationId);

                SubscriptionContext context = new SubscriptionContextBuilder()
                        .setDestinationName(destinationName)
                        .setCorrelationId(correlationId)
                        .setReadTimeOutVal(readTimeOutVal)
                        .setTemporary(isTemporary)
                        .setMessageListener(messageListener)
                        .setSessionId(sessionId)
                        .createSubscriptionContext();
                
                asyncJMSBridge.subscribe(context);
                
               	SuspendResponse<String> r = new SuspendResponse.SuspendResponseBuilder<String>()
               			.entity(topicNameMessage)
						.broadcaster(broadcaster)
						.outputComments(true)
						.header("Connection", "Keep-Alive")
						.addListener(new AtmosphereResourceEventListener() {
							
							@Override
							public void onThrowable(AtmosphereResourceEvent event) {
								LOG.debug("--onThrowable: " + event.broadcaster().getID());
								
							}
							
							@Override
							public void onSuspend(AtmosphereResourceEvent event) {
								LOG.debug("--onSuspend: " + event.broadcaster().getID());
								
							}
							
							@Override
							public void onResume(AtmosphereResourceEvent event) {
								LOG.debug("--onResume: " + event.broadcaster().getID());
								
							}
							
							@Override
							public void onDisconnect(AtmosphereResourceEvent event) {
								LOG.debug("--onDisconnect: " + event.broadcaster().getID());
								
							}
							
							@Override
							public void onBroadcast(AtmosphereResourceEvent event) {
								LOG.debug("--onBroadcast: " + event.broadcaster().getID());
							}

							@Override
							public void onPreSuspend(AtmosphereResourceEvent event) {
								LOG.debug("--onPreSuspend: " + event.broadcaster().getID());
								
							}
						})
						.build();
              
               	return r; 
            }

        } catch (JMSException e) {
            LOG.error("Exception subscribing to topic", e);
        }

        return null;
    }

    /*
     * Method for constructing unique (in case of sendreqeust call)
     * or shared (in case of just listening for events on topic) 
     * string for broadcaster.
     * 
     * @param physicalDestinationName 
     * @param correlationId
     * @return
     */
    private String getID(String physicalDestinationName, String correlationId) {
        if (correlationId != null){
            return ("/"+physicalDestinationName+"/"+correlationId);
        }
        return ("/"+physicalDestinationName);
    }

    /**
     * Lookup on broadcaster to find one. If no related broadcaster
     * was found, create one, otherwise return existing.
     * 
     * @param id
     * @return Broadcaster
     */
    private Broadcaster getBroadcaster(String id) {
    	Broadcaster broadcaster = broadcasterFactory.lookup(JerseyBroadcaster.class, id, true);
    	LOG.debug("Returning broadcaster: {}", broadcaster.getID());
        return broadcaster;
    }


    /**
     * Publishing to the JMS topic.
     * 
     * @param action - publish\ can be omitted
     * @param version - 1.0 or 1.1 \ can be omitted
     * @param message - message to publish
     * @param correlationId - correlation id (if you want to publish to just one subscriber
     * for sendrequest(...) calls).
     * @param replyTo - name of component, should be notified with the publish operation.
     * @return String - </PublishResponse>
     */
	@POST
    public String publish(
            @FormParam("Action") final String action,
            @FormParam("version") final String version,
            @FormParam("Message") final String message,
            @HeaderParam("CorrelationId") final String correlationId,
            @HeaderParam("ReplyTo") final String replyTo
    ) {
    	LOG.debug("--- uponPublish --- corId: " + correlationId + " replyTo: " + replyTo + " destinationName: " + destinationName);

        try {

            /* publish message to topic */
            Message m = jmsBridge.sendTextMessage("TOPIC", destinationName, correlationId, replyTo, null, message, true);

            if (m != null) {

                StringBuilder xml = new StringBuilder();
                xml.append("<PublishResponse><PublishMessageResult><MessageId>");
                xml.append(m.getJMSMessageID());
                xml.append("</MessageId></PublishMessageResult><ResponseMetadata><RequestStatus>SUCCESS</RequestStatus></ResponseMetadata></PublishResponse>");

                return xml.toString();

            }

        } catch (JMSException e) {
            LOG.error("Exception publishing to topic", e);
        }

        return "<PublishResponse><ResponseMetadata><RequestStatus>FAILURE</RequestStatus></ResponseMetadata></PublishResponse>";

    }
}
