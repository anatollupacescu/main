package my.test;

import javax.xml.stream.XMLStreamException;

import me.prettyprint.hector.api.mutation.Mutator;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;
import core.datastore.Cassandra;
import core.datastore.Query;
import core.model.message.XMLMessage;

public class PullNodeTest {

	private static enum op {
		EQ, LTE, LT, GTE, GT
	};

	public static void main(String[] args) throws XMLStreamException {
		XMLMessage message = new XMLMessage("TEST",ActionTest.readFile("src\\resources\\request.xml"));
		Document document = message.getDocument();
		Query[] queries = getQueriesFromDocument(document);
		
		Cassandra ds = Cassandra.getInstance();
		
		Mutator m = ds.getMutator();
		ds.addInsertion(m, "4", "accountNo", "12345");
		ds.addInsertion(m, "4", "name", "Alfred");
		m.execute();
		
		String xml = ds.executeQuery(queries);
		
		System.out.println(xml);
		
	}

	private static Query[] getQueriesFromDocument(Document document) {

		Nodes nodes = document.query("/request/*[@action='retrieve']");

		Query[] queries = new Query[nodes.size()];
		int queryIndex = 0;

		for (int i = 0; i < nodes.size(); i++) {

			Element node = (Element) nodes.get(i);

			String key = node.getAttributeValue("key");
			String columns = node.getAttributeValue("columns");

			String[] keyArr = null;
			String[] colArr = null;

			if (key != null) {
				keyArr = key.split(",");
			}
			if (columns != null) {
				colArr = columns.split(",");
			}

			Elements elements = node.getChildElements();

			Query query = new Query(node.getLocalName(), keyArr, colArr);

			for (int j = 0; key == null && j < elements.size(); j++) {

				Element element = elements.get(j);

				op operation = op.valueOf(element.getAttributeValue("condition"));

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
