package core.node;

import java.util.logging.Level;
import java.util.logging.Logger;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import core.datastore.Cassandra;
import core.datastore.Query;
import core.misc.Const;
import core.misc.XMLBuilder;
import core.model.Message;
import core.model.XMLMessage;

public class PullNode extends GenericNode {

	private static enum op { EQ, LTE, LT, GTE, GT };
	
	private final static Logger logger = Logger.getLogger(PullNode.class.getName());
	
	public PullNode() {
		super(new String[] {});
	}

	@Override
	public String execute(Message obj) {

		Document document = ((XMLMessage)obj).getDocument();
		
		try {
			
			Query[] queries = getQueriesFromDocument(document);

			logger.log(Level.INFO, "queries count " + queries.length);
			
			Cassandra ds = Cassandra.getInstance();

			String xml = ds.executeQuery(queries);
			Document doc = XMLBuilder.build(xml);
			
			Node response = doc.getChild(0);
			for(int i=0; i < response.getChildCount(); i++) {
				Node node = response.getChild(i).copy();
				document.getRootElement().appendChild(node);
			}
			
			return success;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error pulling data", e);
		}
		
		return error;
	}

	private static Query[] getQueriesFromDocument(Document document) {

		Nodes nodes = document.query(Const.RETRIEVE);

		Query[] queries = new Query[nodes.size()];
		int queryIndex = 0;

		for (int i = 0; i < nodes.size(); i++) {

			Element node = (Element) nodes.get(i);

			String key = node.getAttributeValue(Const.KEY);
			String columns = node.getAttributeValue(Const.COLUMNS);

			String[] keyArr = null;
			String[] colArr = null;

			if (key != null) {
				keyArr = key.split(Const.SPLIT_SYMBOL);
			}
			if (columns != null) {
				colArr = columns.split(Const.SPLIT_SYMBOL);
			}

			Elements elements = node.getChildElements();

			Query query = new Query(node.getLocalName(), keyArr, colArr);

			for (int j = 0; key == null && j < elements.size(); j++) {

				Element element = elements.get(j);

				op operation = op.valueOf(element.getAttributeValue(Const.CONDITION));

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
			/*remove the retrieved child*/
//			((Element)document.getChild(0)).removeChild(node);
			node.detach();
			queries[queryIndex++] = query;
		}
		return queries;
	}
}
