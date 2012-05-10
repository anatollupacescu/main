package net.xqwf.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xquery.XQException;

import net.xqwf.Const;
import net.xqwf.helper.DocumentHelper;
import net.xqwf.helper.MiscHelper;
import net.xqwf.helper.XMLHelper;
import net.xqwf.helper.XQueryHelper;
import net.xqwf.model.DocumentWrapper;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.apache.log4j.Logger;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.LocalParameterMap;

public class DocumentService {

	private final static Logger logger = Logger.getLogger(DocumentService.class.getName());
	
	private final static String path = "c:\\Users\\Anatol\\git\\main\\xqwf\\src\\main\\webapp\\WEB-INF\\";
	private final static String error = "error";
	
	public static DocumentWrapper createDocumentFromSubmit(LocalParameterMap map, String list) throws ValidityException, ParsingException, IOException {
		
		if(list == null || map.isEmpty()) return null;
		
		Document document = DocumentHelper.createEmptyDocument();
		
		for (String attributeName : list.split(",")) {
			String attributeValue = map.get(attributeName);
			if (attributeValue != null && !attributeValue.isEmpty()) {
				Element node = new nu.xom.Element(attributeName);
				node.appendChild(attributeValue);
				document.getRootElement().appendChild(node);
				logger.debug("[parseSubmit] added to document: " + attributeName + "=" + attributeValue);
			}
		}
		
		DocumentWrapper wrapper = new DocumentWrapper();
		wrapper.setDocument(document);
		
		return wrapper;
	}

	public void prepareView(LocalAttributeMap view, final DocumentWrapper documentWrapper) {
		
		if(documentWrapper == null) return;
		
		Document document = documentWrapper.getDocument();
		
		Nodes stringValues = document.query("//_next/stringValue");
		
		int size = stringValues.size();
		
		if(size > 0) {
			for(int i = 0; i < size; i++) {
				Element element = (Element)stringValues.get(i);
				String name = element.getAttributeValue("name");
				if(name==null) continue;
				String value = element.getValue();
				view.put(name, value);
				logger.debug("[prepareView] added to view: " + name + "=" + value);
			}
		}
		
		Nodes stringList = document.query("//_next/stringList");
		
		size = stringList.size();
		
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				Element element = (Element)stringList.get(i);
				String name = element.getAttributeValue("name");
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
		
		Nodes objectList = document.query("//_next/objectList");
		
		size = objectList.size();
		
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				List<Map<String, String>> objects = new ArrayList<Map<String, String>>();
				Element object = (Element)objectList.get(i);
				String name = object.getAttributeValue("name");
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
	
	public String executeQuery(String queryFileName, final DocumentWrapper documentWrapper) {
		
		if(queryFileName == null || documentWrapper == null) return error;
		
		Document document = documentWrapper.getDocument();
		
		logger.debug("[executeQuery] file name: " + queryFileName);
		logger.debug("[executeQuery] initial document: " + XMLHelper.format(document));
		
		String queryContent = MiscHelper.readFile(path + queryFileName);
		
		logger.debug("[executeQuery] query content: " + queryContent);
		
		if(queryContent == null) return error;
		
		nu.xom.Document processedDocument = null;
		
		String code = error;
		
		try {
			
			processedDocument = XQueryHelper.processQuery(queryContent, document);
			
			logger.debug("[executeQuery] parsed document: " + XMLHelper.format(processedDocument));
			
			if(pushData(processedDocument)) {
				logger.debug("[executeQuery] done pushing");
			}
			
			if(pullData(processedDocument)) {
				logger.debug("[executeQuery] done pulling data");
			}
			
			code = getResultCode(processedDocument);
			
			documentWrapper.setDocument(processedDocument);
			
		} catch (XQException e) {
			logger.error("[executeQuery] Error processing xquery", e);
		} catch (Exception e) {
			logger.error("[executeQuery] Error executing", e);
		}

		logger.debug("[] returning code " + code);
		
		return code;
	}
	
	private String getResultCode(Document document) {

		if(document == null) return error;
		
		Nodes nodes = document.query("//_next/code");
		
		if(nodes.size() > 0) {
			Node node = nodes.get(0);
			String code = node.getValue();
			if(code != null) {
				node.detach();
				logger.debug("[executeQuery] returning code: " + code);
				return code;
			}
		}
		
		logger.debug("[executeQuery] returning code: " + error);
		
		return error;
	}
	
	private boolean pushData(Document document) {
		
		if(document == null) return false;
		
		String persistQuery = Const.PERSIST_QUERY;
		
		Nodes nodes = document.query(persistQuery);
		
		int pushedCount = 0;
		
		if(nodes.size() > 0) {
			
			pushedCount = DatastoreService.push(nodes);
			
			if(pushedCount > 0) {
				DocumentHelper.removeNodes(Const.PERSIST_QUERY, document);
			}
			
			logger.debug("[pushData] pushed data count: " + pushedCount);
		} else 
			logger.debug("[pushData] Nothing to push");
		
		return pushedCount > 0;
	}
	
	private boolean pullData(Document document) throws Exception {
		
		if(document == null) return false;
		
		Nodes nodes = document.query(Const.RETRIEVE_QUERY);
		
		int appendedCount = 0;
		
		if(nodes.size() > 0) {
			
			Nodes pulledNodes = DatastoreService.pull(nodes);
			
			if(pulledNodes.size() > 0) {
				
				appendedCount = DocumentHelper.appendNodes(pulledNodes, document);
				
				if(appendedCount > 0) {
					DocumentHelper.removeNodes(Const.RETRIEVE_QUERY, document);
				}
			}
			
			logger.debug("[pullData] pulled data count: " + appendedCount);
		} else 
			logger.debug("[pullData] Noting to retrieve");
		
		return appendedCount > 0;
	}
}
