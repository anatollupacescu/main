package core.node;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;
import core.datastore.Cassandra;
import core.datastore.Query;
import core.misc.Const;
import core.misc.XMLBuilder;
import core.model.Message;
import core.model.XMLMessage;

public class PullNode extends Node {

	private static enum op { EQ, LTE, LT, GTE, GT };
	
	public PullNode(String ... args) {
		super(args);
	}

	@Override
	public String execute(Message obj) {

		if(obj == null) return error;
		
		Document document = ((XMLMessage)obj).getDocument();
		
		if(document == null) return error;

		try {
			Query[] queries = getQueriesFromDocument(document);

			Cassandra ds = Cassandra.getInstance();

			String xml = ds.executeQuery(queries);
			Document doc = XMLBuilder.build(xml);
			document.appendChild(doc);
			
			return success;
		} catch (Exception e) {
			e.printStackTrace();
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

			queries[queryIndex++] = query;
		}
		return queries;
	}
}
