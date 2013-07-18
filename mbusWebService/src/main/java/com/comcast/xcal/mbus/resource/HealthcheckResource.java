package com.comcast.xcal.mbus.resource;

import java.util.Properties;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comcast.xcal.mbus.config.IMBusWebServiceProperties;
import com.comcast.xcal.mbus.constant.Constants;
import com.comcast.xcal.mbus.ptp.IJMSBridge;
import com.comcast.xcal.mbus.util.MBusWebServiceUtil;

/**
 * Health check of web service. Some external services can use this in order to
 * monitor activity. Or you can use this as quick check of webservice
 * availability.
 * 
 */
@Path("/healthcheck")
@Component
@Scope("request")
public class HealthcheckResource {

	private static Logger log = LoggerFactory.getLogger("mbusWebServiceLogger");

	@Inject
	private IMBusWebServiceProperties mbusWebServiceProperties;

	@Inject
	private IJMSBridge jmsBridge;

	@GET
	@Produces(MediaType.TEXT_XML)
	public String healthCheck() {

		Properties props = mbusWebServiceProperties.getProperties();

		String testQueue = props.getProperty("xcal.mbus.healthcheckDestination", "XCAL.MBUS.HEALTHCHECK");

		String xmlStr = "<HealthCheckResponse><ResponseMetadata><RequestStatus>SUCCESS</RequestStatus> </ResponseMetadata></HealthCheckResponse>";
		try {
			jmsBridge.sendTextMessage("QUEUE", testQueue, "HealthCheck Test: " + System.currentTimeMillis());
			TextMessage m = (TextMessage) jmsBridge.receiveMessage("QUEUE", testQueue);
			if (m == null) {
				log.error("Expected to receive message sent from HealthCheck but did not.");
			} else {
				jmsBridge.ackMessage(m.getJMSMessageID());
			}
		} catch (JMSException e) {
			// no need to call handleJMSException here since in this method
			// connection is always re-created
			log.error("Exception in the healthcheck", e);
			xmlStr = MBusWebServiceUtil.errorMessageBuilder(Constants.ERR_CODE_INTERNAL_ERROR);
		}

		return xmlStr;
	}

	@GET
	@Produces(MediaType.TEXT_XML)
	@Path("version")
	public String version() {

		Properties props = mbusWebServiceProperties.getProperties();

		String artifactVersion = props.getProperty("xcal.mbus.artifactVersion", "N/A");
		String bomVersion = props.getProperty("xcal.mbus.bomVersion", "N/A");

		String xmlStr = "<Version>" + "<ResponseMetadata>" + "<artifactVersion>" + artifactVersion + "</artifactVersion> " + "<bomVersion>" + bomVersion
				+ "</bomVersion> " + "</ResponseMetadata>" + "</Version>";

		return xmlStr;
	}
}
