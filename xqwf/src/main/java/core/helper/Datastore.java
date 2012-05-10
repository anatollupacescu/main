package core.helper;

import java.util.List;

import me.prettyprint.hector.api.mutation.Mutator;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Logger;

import core.Const;
import core.datastore.Cassandra;
import core.datastore.Query;

public class Datastore {

	private final static Logger logger = Logger.getLogger(Datastore.class.getName());
	
	public static Nodes pull(Nodes nodes) throws Exception { // retrieve

		List<Query> queries = DocumentHelper.getQueriesFromNodes(nodes);
		Nodes returnNodes = new Nodes();
		
		logger.debug("Queries count " + queries.size());
		
		if (queries == null || queries.size() == 0)
			return returnNodes;

		Cassandra ds = Cassandra.getInstance();

		String xml = ds.executeQuery(queries);
		
		logger.debug("Pulled xml");
		logger.debug(XMLHelper.format(xml));
		
		Document doc = DocumentHelper.createDocumentFromString(xml);

		Node response = doc.getChild(0);

		for (int i = 0; i < response.getChildCount(); i++) {
			returnNodes.append(response.getChild(i).copy());
		}

		logger.debug("Pulled nodes count: " + returnNodes.size());
		
		return returnNodes;
	}

	public static int push(Nodes nodes) {

		Cassandra ds = Cassandra.getInstance();
		Mutator<String> m = ds.getMutator();
		
		int i = 0;
		
		for (; i < nodes.size(); i++) {

			Element node = (Element) nodes.get(i);
			
			String action = node.getAttributeValue(Const.ACTION);
			
			if(action == null || !Const.PERSIST.equals(action)) {
				logger.debug("Invalid action for node with index " + i);
				continue;
			}
			
			node.detach();
			
			Elements elements = node.getChildElements();

			String type = node.getLocalName();
			String key = node.getAttributeValue(Const.KEY);
			
			ds.addInsertion(m, key, Const.TYPE, type);
			
			for (int j = 0; j < elements.size(); j++) {

				Element element = elements.get(j);

				String column = element.getLocalName();
				String value = element.getValue();

				ds.addInsertion(m, key, column, value);
				
				logger.debug("inserted key " + key + " column " + column +" value " + value);
			}
		}
		
		m.execute();
		
		return i;
	}
}