package com.comcast.xcal.mbus.pubsub.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.atmosphere.cpr.Broadcaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JMS listener for the webservice. Is used for the 
 * publishing message with broadcaster.
 *
 */
public class MBusJMSMessageListener implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger("mbusWebServiceLogger");

    private final Broadcaster broadcaster;
    private final MBusBroadcasterListener broadcasterListener;
    private final String destinationName;
    private final String correlationId;

    public MBusJMSMessageListener(Broadcaster broadcaster, MBusBroadcasterListener broadcasterListener, String destinationName, String correlationId) {
        this.broadcaster = broadcaster;
        this.broadcasterListener = broadcasterListener;
        this.destinationName = destinationName;
        this.correlationId = correlationId;
    }

    /**
     * When onMessage event happens, we should broadcast to the user
     * message.
     * 
     */
    @Override
	public void onMessage(Message message) {

        /*
         * this is fully async; for blocking and getting completion, see:
         * https://github.com/Atmosphere/atmosphere/wiki/Understanding-Broadcaster
         */

        /* broadcast the messages received */
        final TextMessage textMessage = (TextMessage) message;

        if (textMessage != null) {

            try {

                String text = textMessage.getText();
                LOG.debug("Attempting to broadcast message {}:{} on broadcaster for topic {} / {}", new Object[]{message.getJMSMessageID(), text, destinationName, correlationId});

                if (correlationId != null) {
                    broadcaster.addBroadcasterListener(broadcasterListener);
                }
                broadcaster.broadcast(text);

            } catch (JMSException e) {
                LOG.error("Unable to get message text for textMessage {}", textMessage);
            }

        }

	}

}
