package com.comcast.xcal.mbus.resource;

import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comcast.xcal.mbus.config.IMBusWebServiceProperties;
import com.comcast.xcal.mbus.util.MBusWebServiceUtil;

/**
 * Class to be mapped as discovery servlet. When you are using provided connector API, 
 * with .connect() method, client will try to hit this service. The response would 
 * contain all possible destination and parameters to configure connector.
 *
 */
@Path("/discoveryservice")
@Component
@Scope("request")
public class DiscoveryResource {

    private static Logger log = LoggerFactory.getLogger("mbusWebServiceLogger");
    public static final String CQS_PATH = "queue/";
    public static final String CNS_PATH = "topic/";

    @Inject
    private IMBusWebServiceProperties mbusWebServiceProperties;

    /**
     * This method will take serviceName, serviceVersion to create
     * response for discovery Service(getEndpoint, setEndpoint and errorEndpoint).
     *
     * @param serviceName    String Object to hold service name.
     * @param serviceVersion String Object to hold service version.
     * @param isServer       String Object to hold isServer value.
     * @return String Object of discoveryService response.
     */
    @GET
    @Produces(MediaType.TEXT_XML)
    public String discoveryService(
            @QueryParam("serviceName") String serviceName,
            @QueryParam("serviceVersion") String serviceVersion,
            @QueryParam("isServer") String isServer,
            @QueryParam("serviceMode") String serviceMode
    ) {

        Properties props = mbusWebServiceProperties.getProperties();

        String dsResponse = null;
        StringBuilder baseString = null;
        try {

            String supportedServicesList = props.getProperty("xcal.mbus.supportedServicesList");
            List<String> al = MBusWebServiceUtil.stringToArrayList(supportedServicesList, ",");

            boolean serviceSupported = al.contains(serviceName + "_" + serviceVersion);

            baseString = new StringBuilder(props.getProperty("XMLTag"));

            baseString.append("<timestamp>");
            baseString.append(System.currentTimeMillis());
            baseString.append("</timestamp>");

            baseString.append("<serviceName>");
            baseString.append(serviceName);
            baseString.append("</serviceName>");

            baseString.append("<serviceVersion>");
            baseString.append(serviceVersion);
            baseString.append("</serviceVersion>");

            baseString.append("<queueResourcePath>");
            baseString.append(CQS_PATH);
            baseString.append("</queueResourcePath>");

            baseString.append("<topicResourcePath>");
            baseString.append(CNS_PATH);
            baseString.append("</topicResourcePath>");

            baseString.append(props.getProperty("xmlGetTagStart"));
            if (serviceSupported) {
                baseString.append(CQS_PATH);
                baseString.append(getDestinationName(serviceName, serviceVersion, "CONSUME"));
            }
            baseString.append(props.getProperty("xmlGetTagEnd"));

            baseString.append(props.getProperty("xmlSetTagStart"));
            if (serviceSupported) {
                baseString.append(CQS_PATH);
                baseString.append(getDestinationName(serviceName, serviceVersion, "PRODUCE"));
            }
            baseString.append(props.getProperty("xmlSetTagEnd"));

            baseString.append(props.getProperty("xmlBroadcastTagStart"));
            if (serviceSupported) {
                baseString.append(CNS_PATH);
                baseString.append(getDestinationName(serviceName, serviceVersion, "BROADCAST"));
            }
            baseString.append(props.getProperty("xmlBroadcastTagEnd"));

            baseString.append(props.getProperty("xmlErrorTagStart"));
            if (serviceSupported) {
                baseString.append(CQS_PATH);
                baseString.append(getDestinationName(serviceName, serviceVersion, "ERROR"));
            }
            baseString.append(props.getProperty("xmlErrorTagEnd"));

            baseString.append("<clientID>");
            if (serviceSupported) {
                baseString.append(UUID.randomUUID());
            }
            baseString.append("</clientID>");
            
            if (serviceMode != null && serviceMode.equals("JMS")){
            	baseString.append("<brokerURL>");
            	if (serviceSupported){
            		String broker = StringEscapeUtils.escapeXml(props.getProperty("xcal.mbus.brokerURL"));
            		baseString.append(broker);
            	}
            	baseString.append("</brokerURL>");
            }

            baseString.append(props.getProperty("xmlTerminator"));
            dsResponse = MBusWebServiceUtil.transformerElementToString(MBusWebServiceUtil.stringToElement(baseString.toString()));

        } catch (TransformerException e) {
            log.error("Error in discoveryService : XML transform failed", e);
        }
        return dsResponse;
    }

    private String getDestinationName(String serviceName, String serviceVersion, String opt) {

        Properties props = mbusWebServiceProperties.getProperties();

        String destinationName = null;

        if ( "CONSUME".equalsIgnoreCase(opt)) {
            destinationName = (props.getProperty("queuenameappender")
                    + props.getProperty("xcal.mbus.env")
                    +".TO."
                    + serviceName
                    + "_"
                    + serviceVersion );
        }else if ( "PRODUCE".equalsIgnoreCase(opt)) {
            destinationName = (props.getProperty("queuenameappender")
                    + props.getProperty("xcal.mbus.env")
                    +".FROM."
                    + serviceName
                    + "_"
                    + serviceVersion );
        }else if ("ERROR".equalsIgnoreCase(opt)) {
            destinationName = (props.getProperty("queuenameappender")
                    + props.getProperty("xcal.mbus.env")
                    +".ERROR."
                    + serviceName
                    + "_"
                    + serviceVersion );
        }else if ("BROADCAST".equalsIgnoreCase(opt)) {
            destinationName = (props.getProperty("topicnameappender")
                    + props.getProperty("xcal.mbus.env")
                    +".BROADCAST."
                    + serviceName
                    + "_"
                    + serviceVersion );
        }

        return (destinationName != null) ? destinationName.toUpperCase() : null;
    }
}