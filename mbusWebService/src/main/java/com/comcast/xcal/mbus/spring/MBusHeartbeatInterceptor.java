package com.comcast.xcal.mbus.spring;

import java.util.concurrent.ScheduledExecutorService;

import org.atmosphere.cpr.AtmosphereConfig;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.atmosphere.util.ExecutorsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.comcast.xcal.mbus.config.MBusWebServiceProperties;

public class MBusHeartbeatInterceptor extends HeartbeatInterceptor{
	
		private static final Logger logger = LoggerFactory.getLogger(MBusHeartbeatInterceptor.class);
	    private ScheduledExecutorService heartBeat;
	    private static final String paddingText;
	    private int heartbeatFrequencyInSeconds = 30;

	    static {
	        StringBuffer whitespace = new StringBuffer();
	        for (int i = 0; i < 8192; i++) {
	            whitespace.append(" ");
	        }
	        whitespace.append("\n");
	        paddingText = whitespace.toString();
	    }
	    
	    @Override
	    public void configure(AtmosphereConfig config) {
	        String s = new MBusWebServiceProperties().getProperties().getProperty("xcal.mbus.heartBeat");
	        if (s != null) {
	            heartbeatFrequencyInSeconds = Integer.valueOf(s);
	        }
	        heartBeat = ExecutorsFactory.getScheduler(config);
	    }
}
