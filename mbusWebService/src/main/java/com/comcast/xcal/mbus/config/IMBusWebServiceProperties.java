package com.comcast.xcal.mbus.config;

import java.util.Properties;

/**
 * Interface, describing message bus properties use.
 * In order to provide possibility of changing properties
 * mechanism, current interface was provided.
 * 
 */
public interface IMBusWebServiceProperties {
	
	/**
	 * Describe only one method, returning properties.
	 * 
	 * @return Properties
	 */
    Properties getProperties();
}
