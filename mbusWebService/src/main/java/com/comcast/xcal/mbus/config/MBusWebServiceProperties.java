package com.comcast.xcal.mbus.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Basic implementation of IMBusWebServiceProperties. Is used for handling
 * properties override from capistrano and loading them into shared static variable.
 *
 */
@Component
public class MBusWebServiceProperties implements IMBusWebServiceProperties {
	
	private static Logger log = LoggerFactory.getLogger("mbusWebServiceLogger");
	private static final String PROP_FILE = "/mbusWebService.properties";
	private static Properties prop = new Properties();
	
	/**
	 * Default constructor, containing only loadProperties method call,
	 * to initialize properties with overridden values, if they were provided.
	 */
	public MBusWebServiceProperties() {
        loadProperties();
	}
	
	/**
	 * Reading properties from configured places, and override values, when
	 * they are presented in configOverride file.
	 */
	private void loadProperties() {
		InputStream is;
        Properties overRideProp = new Properties();
		try {
            prop.load(getClass().getResourceAsStream(PROP_FILE));

            String configOverride = System.getProperty("configOverride");
            
            if(configOverride != null) {
                URI overRideFileName = new URI(configOverride);

                is = new FileInputStream(new File(overRideFileName));
                overRideProp.load(is);

                Enumeration e = overRideProp.propertyNames();

                // now override it with the user-defined properties as specified
                // by the configOverride value
                while(e.hasMoreElements()) {
                    String p = (String)e.nextElement();
                    if(overRideProp.getProperty(p) != null) {
                    	if(log.isDebugEnabled())
                    		log.debug("Overriding Property : {} with Value : {}", p, overRideProp.getProperty(p));
                        prop.setProperty(p,overRideProp.getProperty(p));
                    }
                }
            }
		} catch (FileNotFoundException e) {
			log.error("Properties File not found Exception {}", e.getMessage());
		} catch (URISyntaxException e) {
            log.error("Error in loading override properties {}", e.getMessage());
        } catch (IOException e) {
			log.error("Properties File Exception  Exception {}", e.getMessage());
		}
	}
	
	@Override
    public Properties getProperties() {
		return prop;
	}
}