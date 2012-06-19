package net.xqwf;

import java.io.IOException;
import java.io.Serializable;

import org.apache.log4j.Logger;

import com.nosql.datastore.DatastoreService;
import com.nosql.datastore.exception.TransactionException;
import com.nosql.datastore.model.Entity;
import com.nosql.datastore.model.Query;

import net.xqwf.helper.DocumentHelper;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.Serializer;

public class DocumentWrapper implements Serializable {
	
	private final static long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(DocumentWrapper.class.getName());
	
	private Document document = null;
	private DatastoreService datastoreService;
	
	public DocumentWrapper() {
		document = DocumentHelper.createEmptyDocument();
	}
	
	public void setDatastoreService(DatastoreService datastoreService) {
		this.datastoreService = datastoreService;
	}
	
	public void beginTransactions() throws Exception {
		
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

	public void endTransactions() throws TransactionException {
		
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
	
	public String getResultCode() {

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
	
	public boolean saveDocumentData() {
		
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
	
	public boolean retrieveDocumentData() throws Exception {
		
		if(document == null) return false;
		
		Nodes nodes = document.query(Const.RETRIEVE_QUERY);
		
		int appendedCount = 0;
		
		if(nodes.size() > 0) {
			
			Query[] queries = DocumentHelper.nodesToQueryArray(nodes);
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
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		Serializer serializer = new Serializer(out);
		serializer.write(document);
		serializer.flush();
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException {
		document = DocumentHelper.createDocumentFromStream(in);
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document processedDocument) {
		document = processedDocument;
	}
}
