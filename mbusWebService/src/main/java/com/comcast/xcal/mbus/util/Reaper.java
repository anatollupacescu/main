package com.comcast.xcal.mbus.util;


import java.io.IOException;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.pool.PooledConnection;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.activemq.transport.TransportListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.comcast.xcal.mbus.config.IMBusWebServiceProperties;


/**
 * Class, responsible for reaping messages from incoming queue.</br>
 * 
 * Reaper mechanism for removing messages.
 * When message is consumed, to avoid
 * message being lost, copy of message is pushed to the special 
 * incoming queue, so if processing failed, you are able to access
 * message one more time.
 *
 */
public class Reaper {

    public static final String REPLAY_ATTEMPTS_HEADER = "xcal.mbus.reaper.replayAttempts";
    public static final String REPLAY_DESTINATION_NAME_HEADER = "xcal.mbus.reaper.replayDestinationName";
    public static final String REPLAY_DESTINATION_TYPE_HEADER = "xcal.mbus.reaper.replayDestinationType";
    public static final String CONSUMER_OPTIONS = "?reaperConsumer.dispatchAsync=false&reaperConsumer.prefetchSize=100";

    private static final Logger LOG = LoggerFactory.getLogger(Reaper.class);
    private static final int RECEIVE_TIMEOUT = 5;


    private Properties props;

    /* member fields */
    private PooledConnectionFactory reaperConnectionFactory;

    /* lazy init and controlled by initializeReaperConnection */
    boolean connectionFailed = false;
    private Connection reaperConnection = null;
    private Session reaperSession = null;
    private MessageConsumer reaperConsumer = null;
    private MessageProducer reaperProducer = null;

    /* values from configuration */
    private String expiredInProgressDestinationName;
    private String expiredInProgressDestinationType;
    private String deadLetterDestinationName;
    private String deadLetterDestinationType;
    private Integer maxReplayAttempts;

    /**
     * Initialization method for Reaper connection factory
     * 
     * @return Connection
     * @throws Exception
     */
    public Connection initializeReaperConnection() throws Exception {
        if (connectionFailed) {
            try{
                clear();
            }catch(Exception e){
                // do nothing
            }
            reaperConnection = null;
        }
        if (reaperConnection == null) {
            reaperConnection = reaperConnectionFactory.createConnection();
            ((PooledConnection) reaperConnection).getConnection().addTransportListener(new TransportListener() {
                public void onCommand(Object command) {
                }

                public void onException(IOException error) {
                    // set flag to indicate reaperConnection has failed.
                    synchronized (this) {
                        connectionFailed = true;
                    }
                }

                public void transportInterupted() {
                }

                public void transportResumed() {
                }
            });
            synchronized(this){
                connectionFailed = false;
            }
            reaperConnection.start();
            this.reaperSession = reaperConnection.createSession(true, Session.SESSION_TRANSACTED);
            Destination expiredInProgressDestination = createDestination(reaperSession, expiredInProgressDestinationName, expiredInProgressDestinationType);
            this.reaperConsumer = reaperSession.createConsumer(expiredInProgressDestination);
            this.reaperProducer = reaperSession.createProducer(null);
        }
        return reaperConnection;
    }

    /**
     * Shutdown method, close reaper. Clears producer, session, connection.
     * 
     * @throws Exception
     */
    private void clear() throws Exception {
        try{
            if (reaperProducer != null) {
                reaperProducer.close();
            }
            if (reaperSession != null) {
                reaperSession.close();
            }
        }catch(Exception e){
        }finally{
            if(reaperConnection != null) reaperConnection.close();
        }
    }

    /**
     * Constructor for reaper, taking properties and connection factory
     * as arguments.
     * 
     * @param reaperConnectionFactory
     * @param mbusWebServiceProperties
     */
    public Reaper(PooledConnectionFactory reaperConnectionFactory, IMBusWebServiceProperties mbusWebServiceProperties) {

        props = mbusWebServiceProperties.getProperties();

        /* this is the destination that is set up to have a policy destination configured in activemq.xml for messages to expire onto from the in flight queue.*/
        expiredInProgressDestinationName = props.getProperty("xcal.mbus.reaper.expiredInProgressDestinationName", "DLQ.XCAL.MBUS.MESSAGES.INFLIGHT");
        expiredInProgressDestinationType = props.getProperty("xcal.mbus.reaper.expiredInProgressDestinationType", "QUEUE");
        /* any messages that appear in the expiredInProgressDestination will get moved to the DLQ if number of retries is exceeded */
        deadLetterDestinationName = props.getProperty("xcal.mbus.reaper.deadLetterDestinationName", "ActiveMQ.DLQ");
        deadLetterDestinationType = props.getProperty("xcal.mbus.reaper.deadLetterDestinationType", "QUEUE");
        maxReplayAttempts = Integer.parseInt(props.getProperty("xcal.mbus.reaper.maxAttempts", "10"));

        this.reaperConnectionFactory = reaperConnectionFactory;
    }

    /**
     * Method for connecting\creating destination.
     * 
     * @param reaperSession - session
     * @param destinationName - name if the queue or topic
     * @param destinationType - TOPIC or QUEUE
     * @return
     * @throws JMSException
     */
    private Destination createDestination(Session reaperSession, String destinationName, String destinationType) throws JMSException {

        if (destinationType.equalsIgnoreCase("QUEUE")) {
            return reaperSession.createQueue(destinationName + CONSUMER_OPTIONS);
        } else if (destinationType.equalsIgnoreCase("TOPIC")) {
            return reaperSession.createTopic(destinationName + CONSUMER_OPTIONS);
        } else {
            /* TODO: create configuration exception */
            RuntimeException e = new RuntimeException("Invalid destinationType specified: "+ destinationType +" - must match QUEUE or TOPIC");
            LOG.error("Unable to determine destination type", e);
            throw e;
        }

    }

    /**
     * Reaping expired message from the "inprogress" queue.
     */
    public void reapExpiredMessagesFromExpiredInProgressDestination() {

        try {
            initializeReaperConnection();

            if (reaperConsumer == null) {
                throw new IllegalStateException("Reaper not initialized properly.  Has connection been established via failover protocol correctly?");
            }

            TextMessage inProgressMessage = (TextMessage) reaperConsumer.receive(RECEIVE_TIMEOUT);

            while (inProgressMessage != null) {

                int reaperReplayAttempts =  1;

                try {
                    reaperReplayAttempts += inProgressMessage.getIntProperty(REPLAY_ATTEMPTS_HEADER);
                } catch (NumberFormatException e) { /* ignore; just use 1 for reaperReplayAttempts */  }

                String replayDestinationName = inProgressMessage.getStringProperty(REPLAY_DESTINATION_NAME_HEADER);
                String replayDestinationType = inProgressMessage.getStringProperty(REPLAY_DESTINATION_TYPE_HEADER);

                TextMessage replayMessage = reaperSession.createTextMessage(inProgressMessage.getText());
                replayMessage.setIntProperty(REPLAY_ATTEMPTS_HEADER, reaperReplayAttempts);

                Destination destination;

                /* if destination name is null, or we've attempted max number of retries, send to DLQ */
                if (replayDestinationName == null || replayDestinationType == null || reaperReplayAttempts >= maxReplayAttempts) {
                    destination = createDestination(reaperSession, deadLetterDestinationName, deadLetterDestinationType);
                }
                /* otherwise send to replay destination */
                else {
                    destination = createDestination(reaperSession, replayDestinationName, replayDestinationType);
                }

                reaperProducer.send(destination, replayMessage, DeliveryMode.PERSISTENT, inProgressMessage.getJMSPriority(), 0l);

                /* commit in the while loop so that one bad-apple won't spoil the whole lot */
                reaperSession.commit();

                /* consume next message */
                inProgressMessage = (TextMessage) reaperConsumer.receive(RECEIVE_TIMEOUT);
            }


        } catch (Exception e) {

            LOG.error("Unable to recover non-processed expired messages", e);

            try {
                /* rollback the last work we did */
                reaperSession.rollback();
            } catch (JMSException rollbackException) {
                LOG.error("Problem with session rollback", rollbackException);
                throw new RuntimeException(rollbackException);
            }

            throw new RuntimeException(e);

        }
    }

}