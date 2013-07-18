package com.comcast.xcal.mbus.config;

import java.rmi.dgc.VMID;
import java.util.Properties;

import javax.inject.Inject;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;

/**
 * Class, describing spring configuration. Indicates that a class 
 * declares one or more Bean methods and may be processed by the Spring 
 * container to generate bean definitions and service requests for 
 * those beans at runtime.
 *
 */
@Configuration
public class Config {

    private static final Logger LOG = LoggerFactory.getLogger("mbusWebServiceLogger");

    @Inject
    IMBusWebServiceProperties mbusWebServiceProperties;

    /**
     * Initialization of VMID. Vmid is a identifier 
     * that is unique across all Java virtual machines. 
     * VMIDs are used by the distributed garbage collector 
     * to identify client VMs.
     * @return VMID
     */
    @Bean
    public VMID initializeVMID() {
        return new VMID();
    }

    /**
     * Method, for initializing ActiveMQ connection factory.
     * The input parameters are brokerUser, brokerPassword, brokerURL,
     * which are resolved from Properties. 
     * 
     * @return ActiveMQConnectionFactory
     */
    @Bean
    public ActiveMQConnectionFactory initializeActiveMQConnectionFactory() {

        Properties props = mbusWebServiceProperties.getProperties();

        String userId = props.getProperty("xcal.mbus.brokerUserId", ActiveMQConnection.DEFAULT_USER);
        String password = props.getProperty("xcal.mbus.brokerPassword", ActiveMQConnection.DEFAULT_PASSWORD);
        String brokerURL = props.getProperty("xcal.mbus.brokerURL");

        if (brokerURL == null) {
            LOG.error("ActiveMQ Broker URL not found");
        }

        LOG.debug("Initializing ConnectionFactory with: userId {}, password: {}, brokerURL {}",
                new Object[] {userId, password, brokerURL});

        return new ActiveMQConnectionFactory(userId, password, brokerURL);
    }

    /**
     * Wrapper on ActiveMQ ActiveMQConnectionFactory instance to
     * "convert" factory into pooled factory.
     * 
     * @return PooledConnectionFactory
     */
    @Bean
    public PooledConnectionFactory initializePooledConnectionFactory() {

        ActiveMQConnectionFactory factoryOverride = initializeActiveMQConnectionFactory();

        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory(factoryOverride);
        return pooledConnectionFactory;
    }

    /**
     * Wrapper on ActiveMQ ActiveMQConnectionFactory instance to
     * "convert" factory into cached factory.
     * @see <a href="http://static.springsource.org/spring/docs/2.5.x/api/org/springframework/jms/connection/CachingConnectionFactory.html">CachingConnectionFactory</a></br>
     * 
     * @return CachingConnectionFactory
     */    
    @Bean
    public CachingConnectionFactory initializeCachingConnectionFactory() {

        /* get a new connection factory */
        ActiveMQConnectionFactory factoryOverride = initializeActiveMQConnectionFactory();

        /* override prefetch policy - don't want to prefetch with cached consumers */
//        ActiveMQPrefetchPolicy prefetchOverride = new ActiveMQPrefetchPolicy();
//        prefetchOverride.setTopicPrefetch(0);
//        factoryOverride.setPrefetchPolicy(prefetchOverride);

        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(factoryOverride);
        cachingConnectionFactory.setCacheConsumers(false);
        cachingConnectionFactory.setSessionCacheSize(1);
        cachingConnectionFactory.setReconnectOnException(true);

        try {
            cachingConnectionFactory.initConnection();
        } catch (JMSException e) {
           throw new RuntimeException("Unable to initialize CachingConnectionFactory connection");
        }

        return cachingConnectionFactory;
    }

}
