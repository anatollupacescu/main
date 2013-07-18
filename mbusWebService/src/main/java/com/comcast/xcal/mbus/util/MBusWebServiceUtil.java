package com.comcast.xcal.mbus.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.comcast.xcal.mbus.constant.Constants;

/**
 * Class is used as placeholder for the utility methods, used across the whole project/
 *
 */
public class MBusWebServiceUtil {

	private static Logger log = LoggerFactory.getLogger("mbusWebServiceLogger");
	
	/**
	 * Converts a String to an Document Element object (DOM)
	 * 
	 * @param str - incoming xml string 
	 * @return Element
	 * @throws Exception
	 */
	public static Element stringToElement(String str) {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        Element ele = null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new InputSource(new StringReader(str)));
			ele = doc.getDocumentElement();
		} catch (ParserConfigurationException e) {
		} catch(SAXException e) {
		} catch (IOException e) {
		}
        return ele;
	}
	
	/**
	 * Converts a DOM the string representation.
	 * 
	 * @param ele - element to be marshaled.
	 * @return String
	 * @throws TransformerException
	 */
	public static String transformerElementToString(Element ele ) throws TransformerException {
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans;
		try {
        	trans = transfac.newTransformer();
		} catch (TransformerConfigurationException e) {
			log.error("Unable to initialize XML transformer", e);
			return null;
		}
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(ele);
        trans.transform(source, result);
        String xmlString = sw.toString();
		return xmlString;
	}
	
	/**
	 * Method for getting md5 code from the message.
	 * Giving an unique identifier for comparing string and
	 * returning to the user response this value.
	 * 
	 * @param s
	 * @return String
	 */
	public static String getMD5Hash(String s){
		
		String hash= null;
		try{
			MessageDigest m=MessageDigest.getInstance("MD5");
			m.update(s.getBytes(),0,s.length());
			hash = new BigInteger(1,m.digest()).toString(16);			 
		}catch(Exception e){
		e.printStackTrace();
		}
		return hash;		
	}

	/**
	 * Converting string, with some values (as CSV) to the array.
	 * 
	 * @param list
	 * @param separator
	 * @return
	 */
	public static List stringToArrayList(String list, String
			separator) {
		String[] pieces = list.split(separator);
		for (int i = pieces.length - 1; i >= 0; i--) {
			pieces[i] = pieces[i].trim();
		}
		return new ArrayList(Arrays.asList(pieces));
	}
	
	/**
	 * This method will fetch the tracking ID from incoming msg. 
	 * 
	 * @param msg is a String object.
	 * @return String object.
	 * @throws Exception if any exception comes.
	 */
	public static String getTrackingID(String msg) throws Exception{
		String trackingIDStr = null;
		Element ele = stringToElement(msg);
        if(ele != null){
        	NodeList trackingIDNodeList = ele.getElementsByTagName("TrackingId");
        	if((trackingIDNodeList != null) && (trackingIDNodeList.getLength() > 0)){
        		trackingIDStr = trackingIDNodeList.item(0).getFirstChild().getNodeValue();
        	}
        	if(trackingIDStr == null){
        		trackingIDNodeList = ele.getElementsByTagName("trackingID");
            	if((trackingIDNodeList != null) && (trackingIDNodeList.getLength() > 0)){
            		trackingIDStr = trackingIDNodeList.item(0).getFirstChild().getNodeValue();
            	}
        	}
        }
		return trackingIDStr;
	}

	/**
	 * Method, for creating error message, which should be returned to user.
	 * The idea is to add additional nodes to response xml, containing error information,
	 * occurred during web service work.
	 * 
	 * @param errorCode
	 * @param m
	 * @return String
	 */
    public static String errorMessageBuilder(String errorCode, Object... m) {
        String resultErrorCode;
        String resultErrorText;

        if (Constants.KNOWN_ERRORS.containsKey(errorCode)) {
            resultErrorCode = errorCode;
            resultErrorText = Constants.KNOWN_ERRORS.get(errorCode);
        } else {
            resultErrorCode = Constants.ERR_CODE_UNKNOWN;
            resultErrorText = Constants.ERR_TEXT_UNKNOWN;
        }

        try {
            if (m.length == 0)
                resultErrorText = String.format(resultErrorText, "", "", "");
            else
                resultErrorText = String.format(resultErrorText, m);
        } catch (IllegalFormatException e) {
            log.error("unable to format " + resultErrorText);
            // do nothing, just left error string as is
        }

        StringBuilder errorString = new StringBuilder();

        errorString.append("<ErrorResponse><Error><Type>");
        errorString.append("Sender"); // TODO: check out why it is always sender
        errorString.append("</Type><Code>");
        errorString.append(resultErrorCode);
        errorString.append("</Code><Message>");
        errorString.append(resultErrorText);
        errorString.append("</Message><Detail/></Error><RequestId></RequestId></ErrorResponse>");

        return errorString.toString();
    }
}
