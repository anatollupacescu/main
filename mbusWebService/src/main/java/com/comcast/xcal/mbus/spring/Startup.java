package com.comcast.xcal.mbus.spring;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.comcast.xcal.mbus.config.IMBusWebServiceProperties;
import com.comcast.xcal.mbus.ptp.IJMSBridge;
import com.comcast.xcal.mbus.util.CustomTimer;

/**
 * Class, is used for creating and registering reaper. Implementation of ApplicationListener.
 * When context is loaded and we are about to start webservice, we might need to initialize reaper.
 * 
 */
public class Startup implements ApplicationListener<ContextRefreshedEvent> {


    private static Logger log = LoggerFactory.getLogger(Startup.class);

    /**
     * Callback, executed when an ApplicationContext gets initialized or refreshed.
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        ApplicationContext context = event.getApplicationContext();
        final IJMSBridge jmsBridge = context.getBean(IJMSBridge.class);
        final IMBusWebServiceProperties mbusWebServiceProperties = context.getBean(IMBusWebServiceProperties.class);

        log.debug("Initializing: Timer for Reaper");
        Integer delay = Integer.parseInt(mbusWebServiceProperties.getProperties().getProperty("xcal.mbus.reaper.timer.delay", "30"));
        Integer interval = Integer.parseInt(mbusWebServiceProperties.getProperties().getProperty("xcal.mbus.reaper.timer.interval", "10"));
        
        // Schedule reaping on expired messages.
        CustomTimer.getInstance().createRoutineTask(
                new Runnable() {
                    @Override
                    public void run() {
                        jmsBridge.reapExpiredMessages();
                    }
                }, delay, interval, TimeUnit.SECONDS
        );

        /* TODO: register a session expired listener here to clean up the AsyncConsumerCache?? */

    }
}
