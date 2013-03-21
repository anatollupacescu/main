package net.xqwf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;
import javax.xml.xquery.XQSequence;

import net.sf.saxon.xqj.SaxonXQDataSource;
import net.xqwf.misc.Const;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.webflow.core.collection.LocalAttributeMap;

public class DocumentService {

	private final static Logger logger = Logger.getLogger(DocumentService.class.getName());
	private final static XQDataSource ds = new SaxonXQDataSource();
	
	private String path;
	
	public void prepareView(LocalAttributeMap view, final DocumentWrapper documentWrapper) {
		if(documentWrapper == null) {
			return;
		}
		Document document = documentWrapper.getDocument();
		Nodes stringValues = document.query(Const.STRING_VALUE);
		int size = stringValues.size();
		if(size > 0) {
			for(int i = 0; i < size; i++) {
				Element element = (Element)stringValues.get(i);
				String name = element.getAttributeValue(Const.NAME);
				if(name==null) continue;
				String value = element.getValue();
				view.put(name, value);
				logger.debug("[prepareView] added to view: " + name + "=" + value);
			}
		}
		Nodes stringList = document.query(Const.STRING_LIST);
		size = stringList.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				Element element = (Element)stringList.get(i);
				String name = element.getAttributeValue(Const.NAME);
				if(name==null) continue;
				int cc = element.getChildCount();
				List<String> list = new ArrayList<String>();
				for(int j = 0; j < cc; j++) {
					Node child = element.getChild(j);
					list.add(child.getValue());
				}
				if(list.size() > 0) {
					view.put(name, list);
					logger.debug("[prepareView] added to view: " + name + "=" + list);
				}
			}
		}
		Nodes objectList = document.query(Const.OBJECT_LIST);
		size = objectList.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				List<Map<String, String>> objects = new ArrayList<Map<String, String>>();
				Element object = (Element)objectList.get(i);
				String name = object.getAttributeValue(Const.NAME);
				if(name==null) continue;
				int cc = object.getChildCount();
				for(int j = 0; j < cc; j++) {
					Node node = object.getChild(j);
					int objectcc = node.getChildCount();
					Map<String, String> obj = new HashMap<String, String>();
					for (int k = 0; k < objectcc; k++) {
						if (node.getChild(k) instanceof Element) {
							Element element = (Element) node.getChild(k);
							obj.put(element.getLocalName(), element.getValue());
						}
					}
					if(obj.size() > 0) {
						objects.add(obj);
					}
				}
				if(objects.size() > 0) {
					view.put(name, objects);
					logger.debug("[prepareView] added to view: " + name + "=" + objects);
				}
			}
		}
	}
	
	public String executeQuery1(String queryFileName, final DocumentWrapper documentWrapper) throws FileNotFoundException, IOException, XQException, XMLStreamException {
			String fullPath = path + queryFileName + ".xql";
			String query = IOUtils.toString(new FileInputStream(fullPath));
			
			StringBuilder fullQuery =  new StringBuilder();
			fullQuery.append(Const.XQUERY_PREFIX);
			fullQuery.append(query);
			fullQuery.append(";\n\n");
			fullQuery.append(query);
			
			XQSequence sequence = toStream(fullQuery.toString());
			XMLStreamReader reader = sequence.getItemAsStream();
			return getCode(reader);
	}
	
	private String getCode(XMLStreamReader reader) throws XMLStreamException {
		while (reader.hasNext()) {
			int type = reader.getEventType(); 
			if (type == XMLStreamConstants.START_ELEMENT) {
				if(Const.CODE.equals(reader.getLocalName())) {
					reader.next();
					return reader.getText();
				}
			}
			reader.next();
		}
		return null;
	}
	
	public void setPath(String path) {
		this.path = (path.endsWith("/") ? path : path + "/");
	}
	
	public static XQSequence toStream(String query) throws XQException {
        XQConnection conn = ds.getConnection();
        XQPreparedExpression exp = conn.prepareExpression(query);
        XQResultSequence result = exp.executeQuery();
		XQSequence sequence = conn.createSequence(result);
		sequence.next();
		result.close();
		return sequence;
	}
}
