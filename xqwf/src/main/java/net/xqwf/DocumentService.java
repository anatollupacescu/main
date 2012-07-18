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

import com.nosql.datastore.DatastoreService;
import com.nosql.datastore.exception.TransactionException;
import com.nosql.datastore.model.Entity;
import com.nosql.datastore.model.Query;

public class DocumentService {

	private final static Logger logger = Logger.getLogger(DocumentService.class.getName());
	private final MXQueryXQDataSource mxqueryDataSource = new MXQueryXQDataSource();
	
	private String path;
	private DatastoreService datastoreService;
	
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
		
		String code = Const.ERROR;
		
		try {
			
			document = processQuery(queryContent, document);
			documentWrapper.setDocument(document);
			
			logger.debug("[executeQuery] parsed document: " + MiscHelper.formatDocument(document));
			
			beginTransactions(document);
			logger.debug("[executeQuery] done starting transactions");
			
			if(saveDocumentData(document)) {
				logger.debug("[executeQuery] done pushing");
			}
			
			if(retrieveDocumentData(document)) {
				logger.debug("[executeQuery] done pulling data");
			}
			
			endTransactions(document);
			logger.debug("[executeQuery] done commiting/rolling back transactions");
			
			code = getResultCode(document);
			
			logger.debug("[executeQuery] processed document: " + MiscHelper.formatDocument(document));			
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

	public void beginTransactions(Document document) throws Exception {
		
		if(document == null) return;
		
		Nodes nodes = document.query(Const.TRANSACTION_BEGIN);
		
		if(nodes.size() > 0) {
			Element node = (Element)nodes.get(0);
			String name = node.getAttributeValue(Const.NAME);
			if(name != null) {
				node.detach();
				logger.debug("[executeQuery] starting transaction: " + name);
				datastoreService.beginTransaction(name);
			}
		}
	}

	public void endTransactions(Document document) throws TransactionException {
		
		if(document == null) return;
		
		Nodes nodes = document.query(Const.TRANSACTION_COMMIT);

		if (nodes.size() > 0) {
			Element node = (Element) nodes.get(0);
			String name = node.getAttributeValue(Const.NAME);
			if (name != null) {
				node.detach();
				logger.debug("[executeQuery] starting transaction: " + name);
				datastoreService.commitTransaction(name);
			}
		}

		nodes = document.query(Const.TRANSACTION_ROLLBACK);
		
		if (nodes.size() > 0) {
			Element node = (Element) nodes.get(0);
			String name = node.getAttributeValue(Const.NAME);
			if (name != null) {
				node.detach();
				logger.debug("[executeQuery] rolling back transaction: " + name);
				datastoreService.rollbackTransaction(name);
			}
		}
		logger.debug("[executeQuery] returning code: " + Const.ERROR);
	}
	
	public String getResultCode(Document document) {

		if(document == null) return Const.ERROR;
		
		Nodes nodes = document.query(Const.NEXT_CODE);
		
		if(nodes.size() > 0) {
			Node node = nodes.get(0);
			String code = node.getValue();
			if(code != null) {
				node.detach();
				logger.debug("[executeQuery] returning code: " + code);
				return code;
			}
		}
		
		logger.debug("[executeQuery] returning code: " + Const.ERROR);
		
		return Const.ERROR;
	}
	
	public boolean saveDocumentData(Document document) {
		
		boolean result = false;
		
		if(document == null) return false;
		
		String persistQuery = Const.PERSIST_QUERY;
		
		Nodes nodes = document.query(persistQuery);
		
		if(nodes.size() > 0) {
			
			Entity[] entities = DocumentHelper.nodesToEntityArray(nodes);

			try {
				datastoreService.push(entities);
				DocumentHelper.removeNodes(Const.PERSIST_QUERY, document);
				result = true;
			} catch (Exception e) {
				logger.error("Could not push data", e);
			}
			
			logger.debug("[pushData] pushed data count: " + entities.length);
		} else 
			logger.debug("[pushData] Nothing to push");
		
		return result;
	}
	
	public boolean retrieveDocumentData(Document document) throws Exception {
		
		if(document == null) return false;
		
		Nodes nodes = document.query(Const.RETRIEVE_QUERY);
		
		int appendedCount = 0;
		
		if(nodes.size() > 0) {
			
			Query[] queries = DocumentHelper.nodesToQueryArray(nodes);
			
			if(queries == null || queries.length == 0) {
				logger.debug("[pullData] no queries found, resuming");
				return false;
			}
			
			Entity[] entities = datastoreService.pull(queries);
			Node[] pulledNodes = DocumentHelper.entityArrayToNodeArray(entities);
			 
			if(pulledNodes.length > 0) {
				
				Element root = document.getRootElement();
				
				for(Node node : pulledNodes) {
					root.appendChild(node);
					appendedCount++;
				}
				
				if(appendedCount > 0) {
					DocumentHelper.removeNodes(Const.RETRIEVE_QUERY, document);
				}
			}
			
			logger.debug("[pullData] pulled data count: " + appendedCount);
		} else 
			logger.debug("[pullData] Noting to retrieve");
		
		return appendedCount > 0;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public void setDatastoreService(DatastoreService datastoreService) {
		this.datastoreService = datastoreService;
	}
}
