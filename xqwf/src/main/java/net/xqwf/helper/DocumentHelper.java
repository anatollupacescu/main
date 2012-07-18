package net.xqwf.helper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import net.xqwf.Const;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;

import com.nosql.datastore.model.Entity;
import com.nosql.datastore.model.Query;
import com.nosql.datastore.model.QueryCondition;
import com.nosql.datastore.model.QueryOperator;

public class DocumentHelper {

	private final static Builder parser = new Builder();
	
	public static Document createEmptyDocument() {
		Element element = new Element(Const.REQUEST);
		return new nu.xom.Document(element);
	}
	
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
	
	public static Query[] nodesToQueryArray(Nodes nodes) {
		List<Query> queryList = getQueriesFromNodes(nodes);
		Query[] queryArray = new Query[queryList.size()];
		return queryList.toArray(queryArray);
	}

	public static Node[] entityArrayToNodeArray(Entity[] entities) throws XMLStreamException {

		XMLOutputFactory xof = XMLOutputFactory.newInstance();

		Writer stream = new StringWriter();
		XMLStreamWriter xtw = xof.createXMLStreamWriter(stream);
		xtw.writeStartElement(Const.RESPONSE);

		for (Entity entity : entities) {
			Iterator<Entry<String, String>> iterator = entity.getMap().entrySet().iterator();
			xtw.writeStartElement(entity.getType());
			xtw.writeAttribute("key", entity.key);
			while (iterator.hasNext()) {
				Entry<String, String> entry = iterator.next();
				xtw.writeStartElement(entry.getKey());
				xtw.writeCharacters(entry.getValue());
				xtw.writeEndElement();
			}
			xtw.writeEndElement();
		}
		
		xtw.writeEndElement();
		xtw.flush();
		xtw.close();
		
		String xml = stream.toString();
		
		Document document = DocumentHelper.createDocumentFromString(xml);
		Element rootElement = document.getRootElement();
		
		int size = document.getRootElement().getChildCount();
		Node[] nodes = new Node[size];

		for(int i = 0; i < size; i++) {
			nodes[i] = rootElement.getChild(i).copy();
		}
		
		return nodes;
	}

	public static Entity[] nodesToEntityArray(Nodes nodes) {
		int size = nodes.size();
		Entity[] entities = new Entity[nodes.size()];
		Map<String,String> map = null;
		for(int i = 0; i < size; i++) {
			Element node = (Element)nodes.get(i);
			String key = node.getAttributeValue(Const.KEY);
			int cc = node.getChildCount();
			map = new HashMap<String, String>();
			map.put(Const.TYPE, node.getLocalName());
			for(int j = 0; j < cc; j++) {
				Element element = (Element)node.getChild(j);
				String name = element.getLocalName();
				String value = element.getValue();
				if(name != null && value != null) {
					map.put(name, value);
				}
			}
			entities[i] = new Entity(key, map); 
		}
		return entities;
	}
	
	private static List<Query> getQueriesFromNodes(Nodes nodes) {

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
/*
			int childCount = elements.size();
			
			if(keyArr == null && childCount == 0) {
				logger.warn("Either key or query params should be provided for query with index " + i);
				continue;
			}
			*/
			String type = node.getLocalName();
			
			Query query = null;
			
			if(keyArr != null) {
				for(String k : keyArr) {
					query = new Query(k, type, colArr);
					queries.add(query);
				}
			} else {
				
				int size = elements.size();
				
				QueryCondition[] queryConditions = new QueryCondition[size];

				for (int j = 0; j < size; j++) {
					Element element = elements.get(j);
					QueryOperator operation = QueryOperator.valueOf(element.getAttributeValue(Const.CONDITION));
					String column = element.getLocalName();
					String value = element.getValue();
					QueryCondition qc = new QueryCondition(column, operation, value);
					queryConditions[j] = qc;
				}
				query = new Query(type, colArr, queryConditions);
				queries.add(query);
			}
			node.detach();
		}
		return queries;
	}
}
