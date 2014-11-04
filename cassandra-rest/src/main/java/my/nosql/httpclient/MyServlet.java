package my.nosql.httpclient;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import my.nosql.datastore.model.Entity;


public class MyServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	enum Op { get, put, src, del, tget, tput, tsrc, tdel, tbeg, tcom, trol };
	
	protected int appNameLength = -1;
	
	public void init() throws ServletException {
	    appNameLength = getServletConfig().getInitParameter("webApplicationName").length();
	}
	
	protected String marshallEntity(Entity entity) throws XMLStreamException {
		XMLOutputFactory xof = XMLOutputFactory.newInstance();
		
		Writer stream = new StringWriter();
		XMLStreamWriter xtw = xof.createXMLStreamWriter(stream);
		xtw.writeStartElement(entity.getType());
		xtw.writeAttribute("key", entity.getKey());
		Set<Entry<String, String>> entrySet = entity.getMap().entrySet();
		for(Entry<String, String> entry: entrySet) {
			xtw.writeStartElement(entry.getKey());
			xtw.writeCharacters(entry.getValue());
			xtw.writeEndElement();
		}
		
		xtw.writeEndElement();
		xtw.flush();
		xtw.close();
		
		return stream.toString();
	}
	
	protected String[] parseURI(String key) throws Exception {
		//
		String[] tkt = new String[2];

		int lastPos = key.indexOf("/");
		String transaction = key.substring(0, lastPos);
		key = key.substring(lastPos + 1);

		if (transaction == null || transaction.length() == 0) {
			throw new Exception("Malformed transaction");
		}

		if (key == null || key.length() == 0) {
			throw new Exception("Malformed key");
		}

		tkt[0] = transaction;
		tkt[1] = key;

		return tkt;
	}

	protected Map<String, String> requestToMap(HttpServletRequest request) {
		// 		
		Enumeration<?> e = request.getParameterNames();
		
		Map<String,String> map = new HashMap<String,String>(request.getParameterMap().size());
		
		while(e.hasMoreElements()) {
			String name = (String) e.nextElement();
			String value = request.getParameter(name);
			map.put(name, value);
		}
		
		return map;
	}
}
