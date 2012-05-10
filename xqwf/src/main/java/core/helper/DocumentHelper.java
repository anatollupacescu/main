package core.helper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;

import org.apache.log4j.Logger;

import core.Const;
import core.datastore.Query;

public class DocumentHelper {

	public enum Condition { EQ, LTE, LT, GTE, GT };
	
	private final static Logger logger = Logger.getLogger(DocumentHelper.class.getName());
	
	private final static Builder parser = new Builder();
	
	public static Document createDocumentFromString(String xml) {
		try {
			return parser.build(xml, null);
		} catch (ParsingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Document createDocumentFromStream(ObjectInputStream in) {
		try {
			return parser.build(in);
		} catch (ParsingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static int removeNodes(String query, Document document) {
		
		Nodes nodes = document.query(query);
		int i = 0;
		for(; i < nodes.size(); i++) {
			Node n = nodes.get(i);
			n.detach();
		}
		return i;
	}
	
	public static int appendNodes(Nodes nodes, Document document) {

		Element root = document.getRootElement();

		int i = 0;
		for (; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			root.appendChild(node);
		}
		return i;
	}
	
	public static List<Query> getQueriesFromNodes(Nodes nodes) {

		List<Query> queries = new ArrayList<Query>();

		for (int i = 0; i < nodes.size(); i++) {

			Element node = (Element) nodes.get(i);

			String action = node.getAttributeValue(Const.ACTION);
			
			if(action == null || !Const.RETRIEVE.equals(action)) continue;
			
			String key = node.getAttributeValue(Const.KEY);
			String columns = node.getAttributeValue(Const.COLUMNS);

			String[] keyArr = null;
			String[] colArr = null;

			if (key != null && !key.isEmpty()) {
				keyArr = key.split(Const.SPLIT_SYMBOL);
			}
			
			if (columns != null) {
				colArr = columns.split(Const.SPLIT_SYMBOL);
			}

			Elements elements = node.getChildElements();

			int childCount = elements.size();
			
			if(keyArr == null && childCount == 0) {
				logger.warn("Either key or query params should be provided for query with index " + i);
				continue;
			}
			
			Query query = new Query(node.getLocalName(), keyArr, colArr);

			for (int j = 0; key == null && j < elements.size(); j++) {

				Element element = elements.get(j);

				Condition operation = Condition.valueOf(element.getAttributeValue(Const.CONDITION));

				String column = element.getLocalName();
				String value = element.getValue();

				switch (operation) {
				case EQ:
					query.eq(column, value);
					break;
				case GT:
					query.gt(column, value);
					break;
				case GTE:
					query.gte(column, value);
					break;
				case LT:
					query.lt(column, value);
					break;
				case LTE:
					query.lte(column, value);
					break;
				}
			}
			node.detach();
			queries.add(query);
		}
		return queries;
	}

	public static void copyNodes(Document document, Document processedDocument, String keepQuery) {
		
		Nodes nodesToKeep = document.query(keepQuery);
		
		if(nodesToKeep.size() > 0) {
			DocumentHelper.appendNodes(nodesToKeep, processedDocument);
			logger.debug(nodesToKeep.size() + " has been kept back for next node");
		}
	}
}
