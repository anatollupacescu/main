package com.comcast.xcal.mbus.ptp;

import java.util.Properties;

import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.AlreadyClosedException;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.pool.PooledConnection;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.activemq.util.JMSExceptionSupport;
import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.comcast.xcal.mbus.config.IMBusWebServiceProperties;
import com.comcast.xcal.mbus.util.Reaper;

/**
 * Basic implementation of IJMSBridge. Current class is described as "component". 
 * Such classes are considered as candidates for auto-detection when using annotation-based 
 * configuration and classpath scanning.
 *
 */
@Component
public class JMSBridge implements IJMSBridge {

    public static final String JMSHEADER_BROKERPLUGIN_ID_TO_DELETE = "idToDelete";


    private static Logger log = LoggerFactory.getLogger("mbusWebServiceLogger");

    public Properties props;

    private String brokerURL;


    private ActiveMQConnectionFactory connectionFactory = null;


    // producer connection pool.
    private PooledConnectionFactory producerConnectionFactory = null;

    // connection factory for reaper
    private PooledConnectionFactory reaperConnectionFactory = null;

    // Connection pool that pools sessions and consumers - consumer pool is custom built.
    private PooledConnectionFactory consumerConnectionFactory = null;


    private Reaper reaper = null;

    private ConsumerPool consumerPool = null;


    private Long inflightQueueMessageTTL_inMillis = null;
    private long readTimeout = 0L;
    private int consumerPrefetch = 0;
    private long consumerMaxTimeToLive = 0L;
    String inflightDestinationName = null;
    Integer producerSendTimeout;

    boolean bridgeInitialized = false;

    /**
     * Constructor for the JMSBridge. Is automatically injected with properties 
     * and ActiveMQConnectionFactory. 
     * 
     * @param mbusWebServiceProperties
     * @param connectionFactory
     */
    @Inject
    public JMSBridge(IMBusWebServiceProperties mbusWebServiceProperties,
                     ActiveMQConnectionFactory connectionFactory)  {

        this.props = mbusWebServiceProperties.getProperties();

        try{

            if(bridgeInitialized) return;

            if (connectionFactory == null) {
                log.error("IS THIS A UNIT TEST? ActiveMQConnectionFactory not injected properly");
                log.warn("Using development connection factory...");
                connectionFactory = new ActiveMQConnectionFactory(props.getProperty("xcal.mbus.brokerURL"));
            }
            
           
            long connectionExpiryTimeout = Long.parseLong(props.getProperty("xcal.mbus.connectionExpiryTimeout", "10000"));
            producerSendTimeout = Integer.parseInt(props.getProperty("xcal.mbus.producerSendTimeout", "5000"));
            Integer maxProducerConnections = Integer.parseInt(props.getProperty("xcal.mbus.maxProducerConnections", "1"));
            Integer maxConsumerConnections = Integer.parseInt(props.getProperty("xcal.mbus.maxConsumerConnections", "1"));
            Integer maxActiveConsumerSessions = Integer.parseInt(props.getProperty("xcal.mbus.maxActiveConsumerSessions", "500"));
            readTimeout = Long.parseLong(props.getProperty("xcal.mbus.message.readTimeout", "20000"));
            consumerPrefetch = Integer.parseInt(props.getProperty("xcal.mbus.destination.consumer.prefetch", "0"));
            consumerMaxTimeToLive = Integer.parseInt(props.getProperty("xcal.mbus.destination.consumer.maxTimeToLive","1800000"));
            inflightQueueMessageTTL_inMillis = Long.parseLong(props.getProperty("xcal.mbus.message.visibilityTimeout"));
            inflightDestinationName = props.getProperty("xcal.mbus.mbusWebService.inflightDestinationName", "XCAL.MBUS.MESSAGES.INFLIGHT");
            

            log.debug("Initializing JMSBridge...");


            ActiveMQConnectionFactory connectionFactoryWithRedeliveryPolicy = new ActiveMQConnectionFactory(
                    connectionFactory.getUserName(),
                    connectionFactory.getPassword(),
                    connectionFactory.getBrokerURL());


            RedeliveryPolicy policy = connectionFactoryWithRedeliveryPolicy.getRedeliveryPolicy();
            int reDeliveryDelay = Integer.parseInt(props.getProperty("xcal.mbus.InitialReDeliveryDelay"));
            int maxReDeliveryCount = Integer.parseInt(props.getProperty("xcal.mbus.MaxReDelivery"));
            policy.setInitialRedeliveryDelay(reDeliveryDelay);
            policy.setMaximumRedeliveries(maxReDeliveryCount);
            policy.setBackOffMultiplier(2);
            policy.setUseExponentialBackOff(true);
            connectionFactoryWithRedeliveryPolicy.setRedeliveryPolicy(policy);

           
            producerConnectionFactory = new PooledConnectionFactory(connectionFactory);
            producerConnectionFactory.setMaxConnections(maxProducerConnections);
            producerConnectionFactory.setExpiryTimeout(connectionExpiryTimeout);
            producerConnectionFactory.setIdleTimeout(0);
            producerConnectionFactory.start();

            reaperConnectionFactory = new PooledConnectionFactory(connectionFactory);
            reaperConnectionFactory.setExpiryTimeout(connectionExpiryTimeout);
            reaperConnectionFactory.setIdleTimeout(0);
            reaperConnectionFactory.start();
            reaper = new Reaper(reaperConnectionFactory, mbusWebServiceProperties);
            reaper.initializeReaperConnection();

           
            consumerConnectionFactory  = new PooledConnectionFactory(connectionFactoryWithRedeliveryPolicy);
            consumerConnectionFactory.setMaxConnections(maxConsumerConnections);
            consumerConnectionFactory.setMaximumActive(maxActiveConsumerSessions);
            consumerConnectionFactory.setExpiryTimeout(connectionExpiryTimeout);
            consumerConnectionFactory.setIdleTimeout(0);
            consumerConnectionFactory.start();

            

            consumerPool = new ConsumerPool(consumerConnectionFactory);

            bridgeInitialized = true;
            log.debug("JMSBridge initialized.");

        } catch (Exception e) {
            stop();
            throw new RuntimeException(e);
        }

    }

    /**
     * To have gentle cleanup when shutdown, pools and factories
     * Should be cleaned and closed.
     */
    public void stop() {
        try {
        	if (consumerPool != null) {
        		consumerPool.close();
        	}
            if (producerConnectionFactory != null){
                producerConnectionFactory.stop();
            }
            if (reaperConnectionFactory != null) {
                reaperConnectionFactory.stop();
            }
            if (consumerConnectionFactory != null){
                consumerConnectionFactory.stop();
            }
        }catch(Exception exp){
            log.error("Error cleaning up resources: {}", exp.getMessage());
        }finally{
            bridgeInitialized=false;
        }
    }


    @Override
    public Message receiveMessage(String destinationType, String destinationName) throws JMSException {
        return receiveMessage(destinationType, destinationName, null, readTimeout);
    }

    @Override
    public Message receiveMessage(String destinationType, String destinationName, long timeout) throws JMSException {
        return receiveMessage(destinationType, destinationName, null, timeout);
    }

    @Override
    public Message receiveMessage(String destinationType, String destinationName, String selector, long timeout) throws JMSException{
        Message m = null;
        Session session = null;
        PooledConsumer pooledConsumer = null;
        DestinationKey key = null;
        MessageProducer producer = null;
        Message t = null;
        try {
            key = new DestinationKey(destinationType, destinationName, selector);
            pooledConsumer = consumerPool.borrowConsumer(key);
            session = pooledConsumer.getSession();

            m = pooledConsumer.receive(timeout);

            Destination destination = null;


            if (m != null && "QUEUE".equalsIgnoreCase(destinationType)) {

                t = session.createTextMessage(((TextMessage) m).getText());

                t.setJMSMessageID(m.getJMSMessageID());
                t.setJMSPriority(m.getJMSPriority());
                t.setJMSCorrelationID(m.getJMSCorrelationID());
                if (m.getStringProperty("replyTo") != null) {
                	t.setStringProperty("replyTo", m.getStringProperty("replyTo"));
                }

                t.setStringProperty(Reaper.REPLAY_DESTINATION_NAME_HEADER, destinationName);
                t.setStringProperty(Reaper.REPLAY_DESTINATION_TYPE_HEADER, destinationType);

                if (m.propertyExists(Reaper.REPLAY_ATTEMPTS_HEADER)) {
                    t.setIntProperty(Reaper.REPLAY_ATTEMPTS_HEADER, m.getIntProperty(Reaper.REPLAY_ATTEMPTS_HEADER));
                } else {
                    t.setIntProperty(Reaper.REPLAY_ATTEMPTS_HEADER, 0);
                }

                destination = session.createQueue(inflightDestinationName);
                producer = session.createProducer(null);
                producer.send(destination, t, DeliveryMode.PERSISTENT, t.getJMSPriority(), inflightQueueMessageTTL_inMillis);
                session.commit();

            }

        } catch (JMSException e) {
            // Do not close session as that will close the consumers and returns the session to sessionPool...
            // just rollback the session
            if (session != null) {
                session.rollback();
            }
            if (pooledConsumer != null) {
                consumerPool.invalidateConsumer(key, pooledConsumer);
                pooledConsumer = null;
            }
            throw e;
        } catch (IllegalStateException e1) {
            if (pooledConsumer != null) {
                consumerPool.invalidateConsumer(key, pooledConsumer);
                pooledConsumer = null;
            }
        } finally {
            try {
                if (producer != null) {
                    producer.close();
                }
            } catch (Exception ignored) {
                log.warn("Unable to close producer: {}", ignored);
            }
            if (pooledConsumer != null) {
                consumerPool.returnConsumer(key, pooledConsumer);
                pooledConsumer = null;
            }
        }
        return t;
    }

    @Override
    public TextMessage sendTextMessage(String destinationType, String destinationName, String message) throws JMSException  {
        return sendTextMessage(destinationType, destinationName, null, null, message);
    }

    @Override
    public TextMessage sendTextMessage(String destinationType, String destinationName, String correlationId, String message) throws JMSException {
        return sendTextMessage(destinationType, destinationName, correlationId, null, message);
    }

    @Override
    public TextMessage sendTextMessage(String destinationType, String destinationName, String correlationId, String replyTo, String message) throws JMSException {       TextMessage m = null;
    	return sendTextMessage(destinationType, destinationName, correlationId, replyTo, null, message);
    }
    
    @Override
    public TextMessage sendTextMessage(String destinationType, String destinationName, String correlationId, String replyTo, Integer priority, String message) throws JMSException {
        return sendTextMessage(destinationType, destinationName, correlationId, replyTo, priority, message, false);
    }

    @Override
    public TextMessage sendTextMessage(String destinationType, String destinationName, String correlationId, String replyTo, Integer priority, String message, Boolean nonPersistent) throws JMSException {
    	TextMessage m = null;
        Connection connection=null;
        MessageProducer producer=null;
        Session session=null;
        
        try{
            connection = producerConnectionFactory.createConnection();
            ((PooledConnection)connection).getConnection().setSendTimeout(producerSendTimeout);
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(null);
            Destination destination=null;
            if ( "QUEUE".equalsIgnoreCase(destinationType) ){
                destination = session.createQueue(destinationName);
            }else if ("TOPIC".equalsIgnoreCase(destinationType)){
                destination = session.createTopic(destinationName);
            }
            m = session.createTextMessage(message);

            if (correlationId != null) {
                m.setJMSCorrelationID(correlationId);
            }

            if (replyTo != null) {
                m.setStringProperty("replyTo", replyTo);
            }

            int deliveryMode = DeliveryMode.PERSISTENT;
            if (nonPersistent) {
                deliveryMode = DeliveryMode.NON_PERSISTENT;
            }

            // JMSPriority  0-lowest, 9-highest
            if (priority != null) {
                m.setJMSPriority(priority);
                producer.send(destination, m, deliveryMode, priority, 0L);
            } else {
                producer.send(destination, m, deliveryMode, producer.getPriority(),0L);
            }
        }catch(JMSException e){
            log.error( "Received exception in sendTextMessage: {}", e.getMessage());
            throw e;
        }finally {
            try{
                if( producer != null ) producer.close();
            }catch(JMSException ignored){
            }
            try {
                if (session != null) session.close();
            }catch(JMSException ignored) {
            }
            try{
                if (connection != null) connection.close();
            }catch(JMSException ignored){
            }
        }
        return m;
    }

    @Override
    public boolean ackMessage(String messageId) throws JMSException {

        boolean retValue=false;
        TextMessage m = null;
        Connection connection=null;
        MessageProducer producer=null;
        Session session=null;
        try{
            connection = producerConnectionFactory.createConnection();
            ((PooledConnection)connection).getConnection().setSendTimeout(producerSendTimeout);
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(null);
            String toDeleteFromInflightDestinationName = props.getProperty("xcal.mbus.mbusWebService.toDeleteFromInflightDestinationName", "TO_DELETE_FROM_INFLIGHT");
            Destination destination=null;
            destination = session.createTopic(toDeleteFromInflightDestinationName);
            m = session.createTextMessage(messageId);
            m.setStringProperty(JMSHEADER_BROKERPLUGIN_ID_TO_DELETE, messageId);
            producer.send(destination, m, DeliveryMode.PERSISTENT, producer.getPriority(),0L);
            retValue=true;
        }catch(JMSException e){
            log.error( "Received exception in sendTextMessage: " + e.getMessage());
            throw e;
        }finally {
            try{
                if( producer != null ) producer.close();
            }catch(JMSException ignored){
            }
            try {
                if (session != null) session.close();
            }catch(JMSException ignored) {
            }
            try{
                if (connection != null) connection.close();
            }catch(JMSException ignored){
            }
        }
        return retValue;

    }

    @Override
    public void reapExpiredMessages() {
        reaper.reapExpiredMessagesFromExpiredInProgressDestination();
    }

    @Override
    protected void finalize() throws Throwable {
    	stop();
    }

    private class ConsumerPool {

        private GenericKeyedObjectPool pool;
        private KeyedPoolableObjectFactory consumerFactory;

        public ConsumerPool(PooledConnectionFactory connectionFactory){

            consumerFactory = new PooledConsumerFactory(connectionFactory);

            pool = new GenericKeyedObjectPool(consumerFactory);

            pool.setMaxActive(Integer.parseInt(props.getProperty("xcal.mbus.destination.consumer.maxActive")));
            pool.setMaxIdle(Integer.parseInt(props.getProperty("xcal.mbus.destination.consumer.maxIdle")));

            Byte whenExhaustedAction = Byte.parseByte(props.getProperty("xcal.mbus.destination.consumer.whenExhaustedAction"));
            pool.setWhenExhaustedAction(whenExhaustedAction);
            pool.setLifo(false);

            // settings for evicting idle consumers
            pool.setMinIdle(Integer.parseInt(props.getProperty("xcal.mbus.destination.consumer.minIdle")));
            pool.setTimeBetweenEvictionRunsMillis(Long.parseLong(props.getProperty("xcal.mbus.destination.consumer.evictionRunInterval")));
            pool.setMinEvictableIdleTimeMillis(Long.parseLong(props.getProperty("xcal.mbus.destination.consumer.maxIdleTime")));
            pool.setTestWhileIdle(Boolean.parseBoolean(props.getProperty("xcal.mbus.destination.consumer.testWhileIdle", "false")));
            pool.setNumTestsPerEvictionRun(Integer.parseInt(props.getProperty("xcal.mbus.destination.consumer.testsPerEvictionRun")));
            pool.setTestOnBorrow(Boolean.parseBoolean(props.getProperty("xcal.mbus.destination.consumer.testOnBorrow", "false")));
            pool.setTestOnReturn(Boolean.parseBoolean(props.getProperty("xcal.mbus.destination.consumer.testOnReturn", "false")));


        }

        public synchronized void close() throws Exception {
            if (pool != null) {
                clear();
                pool.close();
            }
            pool = null;
        }

		public synchronized PooledConsumer borrowConsumer(Object key) throws JMSException {
            try {
                Object object = getConsumerPool().borrowObject(key);
                PooledConsumer consumer = (PooledConsumer)object;

                if (log.isDebugEnabled()) {
                    logDebugStats("Borrowing Consumer", key, consumer);
                }

                if (consumer.isValid()) {
                    return consumer;
                } else {
                    log.warn("Invalidating Borrowed Consumer: " + consumer);
                    getConsumerPool().invalidateObject(key, consumer);
                    return borrowConsumer(key);
                }
            } catch (JMSException e) {
                throw e;
            } catch (Exception e) {
                log.warn("Problem borrowing consumer: {}", e);
                throw JMSExceptionSupport.create(e);
            }
        }

        public synchronized void returnConsumer(Object key, PooledConsumer consumer) throws JMSException {
            if(consumer == null) return;
            try {

                if (log.isDebugEnabled()) {
                    logDebugStats("Returning Consumer", key, consumer);
                }


                
                if (!consumer.isValid()) {
                    log.warn("Invalidating Consumer instead of returning " + consumer);
                    getConsumerPool().invalidateObject(key,consumer);
                } else if(consumer.hasLivedTooLong()) {
                    log.debug("Consumer lived too long, invalidating instead of returning " + consumer);
                    getConsumerPool().invalidateObject(key,consumer);
                } else {
                    getConsumerPool().returnObject(key,consumer);

                }
            } catch (Exception e) {
                log.warn("Unable to return " + consumer + " to pool: " + e, e);
                throw JMSExceptionSupport.create("Failed to return consumer to pool: " + e, e);
            }
        }

        private void logDebugStats(String prefix, Object key, PooledConsumer consumer) throws AlreadyClosedException {
            final int numActive = getConsumerPool().getNumActive(key);
            final int numIdle = getConsumerPool().getNumIdle(key);
            log.debug("{}: {} for {} [{} active] [{} idle] ", new Object[] {prefix, consumer, consumer.destination, numActive, numIdle});
        }

        public synchronized void invalidateConsumer(Object key, PooledConsumer consumer) throws JMSException {
            if(consumer == null) return;
            try {
                getConsumerPool().invalidateObject(key,consumer);
                consumer = null;
            } catch (Exception e) {
                log.warn("Unable to invalidate " + consumer + " from pool: " + e, e);
                throw JMSExceptionSupport.create("Failed to invalidate consumer: " + e, e);
            }
        }

        // Implementation methods
        // -------------------------------------------------------------------------
        protected KeyedObjectPool getConsumerPool() throws AlreadyClosedException {
            if (pool == null) {
                throw new AlreadyClosedException();
            }
            return pool;
        }

        protected void clear() throws Exception {
            try{
                if ( pool != null) pool.clear();
            }catch(Exception e){
            }
        }

    }

    private class PooledConsumerFactory implements KeyedPoolableObjectFactory {
    	private PooledConnectionFactory connectionFactory;

        public PooledConsumerFactory(PooledConnectionFactory connectionFactory){
            this.connectionFactory = connectionFactory;
        }

        public Object makeObject(Object key) throws Exception {
        	log.debug("Creating new consumer...");
             return new PooledConsumer((DestinationKey)key, connectionFactory);
        }


        public void destroyObject(Object key, Object obj) throws Exception {
             PooledConsumer consumer = (PooledConsumer)obj;
             consumer.close();
        }

		public void activateObject(Object key, Object obj) throws Exception {
			// TODO Auto-generated method stub
		}

		public void passivateObject(Object key, Object obj) throws Exception {
			// TODO Auto-generated method stub
		}

		public boolean validateObject(Object key, Object obj) {
			 PooledConsumer consumer = (PooledConsumer)obj;
			 return consumer.isValid();
		}
    }

    private class PooledConsumer {
    	private MessageConsumer delegate=null;
    	private Session session=null;
    	private Connection connection = null;
    	Destination destination=null;
        private final Long timeStarted;

    	
    	 boolean isValid = false;
    	
    	 public PooledConsumer(DestinationKey key, PooledConnectionFactory connectionFactory) throws Exception {
    		 try {
                connection = getConnection(connectionFactory);
                log.debug("Using Connection: " + connection);
                session = connection.createSession(true, Session.SESSION_TRANSACTED);
                log.debug("Using session: " + session);
                
	                if ( "QUEUE".equalsIgnoreCase(key.getDestinationType()) ){
	                    destination = session.createQueue(key.getDestinationName()+"?consumer.dispatchAsync=false&consumer.prefetchSize="+consumerPrefetch);
	                }else if ("TOPIC".equalsIgnoreCase(key.getDestinationType())){
	                    destination = session.createTopic(key.getDestinationName());
	                }
                    delegate = session.createConsumer(destination, key.getSelector());
                    log.info("Created consumer" + delegate);
                    isValid=true;
                    timeStarted = System.currentTimeMillis();
            }catch (Exception e) {
                try{
                    if (session != null) {
                        session.close();
                    }
                }catch(JMSException jmsexp){
                }
                try{
                    if (connection != null) {
                        connection.close();
                        connection = null;
                    }
                }catch(JMSException jmsexp){
                }
                throw e;
            }
    	}
    	
		public Session getSession() {
			return session;
		}
		
		public Message receive(long timeout) throws JMSException{
			return delegate.receive(timeout);
		}
		
		protected Connection getConnection(PooledConnectionFactory connectionFactory) throws Exception {
         	Connection connection = connectionFactory.createConnection();
        /*    ((PooledConnection)connection).getConnection().addTransportListener(new TransportListener() {
                public void onCommand(Object command) {
                }

                public void onException(IOException error) {
                    // set flag to indicate connection has failed.
                    synchronized (this) {
                    	log.debug("Connection failed...");
                        useable = false;
                    }
                }

                public void transportInterupted() {
                }

                public void transportResumed() {
                }
            });
         */
         	connection.setExceptionListener(new ExceptionListener() {
                
                public void onException(JMSException error){
                    // set flag to indicate connection has failed.
                    synchronized (this) {
                    	log.info("Connection failed ...");
                        isValid = false;
                    }
                }

            });
            connection.start();
      
            return connection;
        }
		
		 
		 public void close() {
			 try{
				 log.info("Closing consumer : " + delegate);
				try {
					if (delegate != null)
						delegate.close();
				} catch (JMSException e1) {
					log.debug("Exception trying to close consumer", e1);
				}
				try {
					if (session != null)
						session.close();
				} catch (JMSException e2) {
					log.debug("Exception trying to close session", e2);
				}
				try {
					if (connection != null)
						connection.close();
				} catch (JMSException e3) {
					log.debug("Exception trying to close connection", e3);
				}
			 } finally{
				 isValid = false;
				 delegate = null;
				 session = null;
				 connection = null;
			 }
		 }
		 
		 public boolean isValid(){
			 // consumer is valid if both MessageConsumer and Session are not null and the connection is not closed
			 // TODO Implement more elegant logic in here
			 return  isValid && (delegate != null) && (session != null);
		 }

         private boolean hasLivedTooLong() {
             return System.currentTimeMillis() - timeStarted > consumerMaxTimeToLive;
         }
		
    }
    
    private class DestinationKey {
		private String destinationType=null;
		private String destinationName=null;
		private String selector=null;

		public DestinationKey(String destinationType, String destinationName, String selector){
			this.destinationType=destinationType;
			this.destinationName=destinationName;
			this.selector=selector;
		}

		public String getDestinationType() {
			return destinationType;
		}

		public String getDestinationName() {
			return destinationName;
		}


		public String getSelector() {
			return selector;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime
					* result
					+ ((destinationName == null) ? 0 : destinationName
							.hashCode());
			result = prime
					* result
					+ ((destinationType == null) ? 0 : destinationType
							.hashCode());
			result = prime * result
					+ ((selector == null) ? 0 : selector.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DestinationKey other = (DestinationKey) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (destinationName == null) {
				if (other.destinationName != null)
					return false;
			} else if (!destinationName.equals(other.destinationName))
				return false;
			if (destinationType == null) {
				if (other.destinationType != null)
					return false;
			} else if (!destinationType.equals(other.destinationType))
				return false;
			if (selector == null) {
				if (other.selector != null)
					return false;
			} else if (!selector.equals(other.selector))
				return false;
			return true;
		}

		private JMSBridge getOuterType() {
			return JMSBridge.this;
		}
	}

}
