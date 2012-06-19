package net.xqwf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQSequence;

import net.xqwf.helper.DocumentHelper;
import net.xqwf.helper.MiscHelper;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.apache.log4j.Logger;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.LocalParameterMap;

import ch.ethz.mxquery.xqj.MXQueryXQDataSource;

public class DocumentService {

	private final static Logger logger = Logger.getLogger(DocumentService.class.getName());
	private final MXQueryXQDataSource mxqueryDataSource = new MXQueryXQDataSource();
	
	private String path;

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
	
	public String executeQuery(String queryFileName, final DocumentWrapper documentWrapper) {
		
		if(queryFileName == null || documentWrapper == null) return Const.ERROR;
		
		Document document = documentWrapper.getDocument();
		
		logger.debug("[executeQuery] file name: " + queryFileName);
		logger.debug("[executeQuery] initial document: " + MiscHelper.formatDocument(document));
		
		String queryContent = MiscHelper.readFile(path + queryFileName);
		
		logger.debug("[executeQuery] query content: " + queryContent);
		
		if(queryContent == null) return Const.ERROR;
		
		nu.xom.Document processedDocument = null;
		
		String code = Const.ERROR;
		
		try {
			
			processedDocument = processQuery(queryContent, document);
			documentWrapper.setDocument(processedDocument);
			
			logger.debug("[executeQuery] parsed document: " + MiscHelper.formatDocument(processedDocument));
			
			documentWrapper.beginTransactions();
			logger.debug("[executeQuery] done starting transactions");
			
			if(documentWrapper.saveDocumentData()) {
				logger.debug("[executeQuery] done pushing");
			}
			
			if(documentWrapper.retrieveDocumentData()) {
				logger.debug("[executeQuery] done pulling data");
			}
			
			documentWrapper.endTransactions();
			logger.debug("[executeQuery] done commiting/rolling back transactions");
			
			code = documentWrapper.getResultCode();
			
		} catch (XQException e) {
			logger.error("[executeQuery] Const.ERROR processing xquery", e);
		} catch (Exception e) {
			logger.error("[executeQuery] Const.ERROR executing", e);
		}

		logger.debug("[] returning code " + code);
		
		return code;
	}
	
	private Document processQuery(String query, Document document) throws XQException {
		XQConnection xqconnection = mxqueryDataSource.getConnection();
		XQPreparedExpression exp = xqconnection.prepareExpression(query);
		exp.bindDocument(new QName(Const.DOCUMENT), document.toXML(), null,null);
		XQSequence xqsequence = exp.executeQuery();
		OutputStream result = new ByteArrayOutputStream();
		xqsequence.writeSequence(result, null);
		return DocumentHelper.createDocumentFromString(result.toString());
	}
	
	public void setPath(String path) {
		this.path = path;
	}
}
