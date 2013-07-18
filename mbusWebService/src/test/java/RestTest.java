import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.ObjectInputStream.GetField;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



public class RestTest {

	
	private static String baseURL = "http://10.141.113.20:8080/MBusWebService/message/";
	
	public static String getMessage(String queueName,int i) {
		
		String retMessage = null;
		
		try{
			String url = baseURL + queueName + "?Action=ReceiveMessage&version=1.0&ClientID=" + i;
			URL requestEndpoint =new URL(url) ;
			HttpURLConnection httpConnection = (HttpURLConnection) requestEndpoint.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setAllowUserInteraction(false);
			httpConnection.setDoOutput(false);
			httpConnection.setRequestProperty("Content-type","text/xml");
			httpConnection.connect();
			
			BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(httpConnection.getInputStream()) );
			StringBuffer strBuf = new StringBuffer();
			
			while( bufferedReader.ready()){
				strBuf.append(bufferedReader.readLine());
			}
			bufferedReader.close();
			httpConnection.disconnect();
			
		
			retMessage = strBuf.toString();
			//message = (Message) XMLToObject(strBuf.toString());
	 
		}catch(Exception e) {
			e.printStackTrace();
		}
		return retMessage;
	}
	
	
	
	
	
	
	public static void deleteMessage(String queueName,String messageId, String ack){
		
		String retMessage = "";

		try{
			String url = baseURL + queueName + "?Action=DeleteMessage&version=1.0&MessageId=" + messageId + "&AckToken="+ ack;
			URL requestEndpoint =new URL(url) ;
			HttpURLConnection httpConnection = (HttpURLConnection) requestEndpoint.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setAllowUserInteraction(false);
			httpConnection.setDoOutput(false);
			httpConnection.setRequestProperty("Content-type","text/xml");
			httpConnection.connect();
			
			BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(httpConnection.getInputStream()) );
			StringBuffer strBuf = new StringBuffer();
			
			while( bufferedReader.ready()){
				strBuf.append(bufferedReader.readLine());
			}
			bufferedReader.close();
			httpConnection.disconnect();
		
			retMessage = strBuf.toString();
			
			
		}catch(Exception e) {
            e.printStackTrace();
		}
		//return retMessage;		
				
	}
	
	
	
	
	public static void writeMessage(String queueName, String message){
	
		String retMessage = "";
		try{
			String encodedStr = URLEncoder.encode(message, "UTF-8");
			String urlstr = baseURL + queueName + "?Action=SendMessage&version=1.0&MessageBody=" + encodedStr;				
			URL requestEndpoint =new URL(urlstr) ;
			HttpURLConnection httpConnection = (HttpURLConnection) requestEndpoint.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setAllowUserInteraction(false);
			httpConnection.setDoOutput(false);
			httpConnection.setRequestProperty("Content-type","text/xml");
			httpConnection.connect();		
			BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(httpConnection.getInputStream()) );
			StringBuffer strBuf = new StringBuffer();		
			while( bufferedReader.ready()){
				strBuf.append(bufferedReader.readLine());
			}
			bufferedReader.close();
			httpConnection.disconnect();
		
			retMessage = strBuf.toString();	
		
	}catch(Exception e) {
		e.printStackTrace();
	}
		
}


	
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int i = 0;
		
		while( i < 100){
			i++;
			System.out.println(i);
			writeMessage("arijit","testtest");
			String in = getMessage("arijit",i);
			try{
				String ack = getAck(in);
				deleteMessage("arijit","",ack);
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
						
		}		
	}
	
	public static String getAck(String msg) throws Exception{
		String trackingIDStr = null;
		Element ele = stringToElement(msg);
        if(ele != null){
        	NodeList trackingIDNodeList = ele.getElementsByTagName("AckToken");
        	if((trackingIDNodeList != null) && (trackingIDNodeList.getLength() > 0)){
        		trackingIDStr = trackingIDNodeList.item(0).getFirstChild().getNodeValue();
        	}
        	if(trackingIDStr == null){
        		trackingIDNodeList = ele.getElementsByTagName("AckToken");
            	if((trackingIDNodeList != null) && (trackingIDNodeList.getLength() > 0)){
            		trackingIDStr = trackingIDNodeList.item(0).getFirstChild().getNodeValue();
            	}
        	}
        }
		return trackingIDStr;
	}
	
	
	
	/**
	 * Converts a String to an Document Element object
	 * @param str
	 * @return
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
		//	log.error("Error parsing string : " + str + " : message : " + e.getMessage());
		} catch(SAXException e) {
			//log.error("SAX Error parsing string : " + str + " : message : " + e.getMessage());
		} catch (IOException e) {
			//log.error("IO Error parsing string : " + str + " : message : " + e.getMessage());
		}
        return ele;
	}


}
