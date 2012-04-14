package my.test;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;
import core.datastore.pull.Query;
import core.model.message.XMLMessage;

public class PullNodeTest {

	private static enum op { EQ, LTE, LT, GTE, GT };
	
	public static void main(String[] args) {
		XMLMessage message = new XMLMessage("TEST", ActionTest.readFile("src\\resources\\request.xml"));
		Document document = message.getDocument();
		Nodes nodes = document.query("/request/*[@action='retrieve']");
		Query[] queries = new Query[nodes.size()];
		int queryIndex = 0;
		for(int i = 0; i < nodes.size(); i++) {
			
			Element node = (Element) nodes.get(i);
			Elements elements = node.getChildElements();
			
			Query query = new Query(node.getLocalName());
			
			for(int j = 0; j < elements.size(); j++) {
				
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
			
			String columsString = node.getAttributeValue("condition");
			if (columsString != null) {
				String[] columnArray = columsString.split(",");
				query.columns(columnArray);
			}
			
			queries[queryIndex++] = query;
		}
	}
}
