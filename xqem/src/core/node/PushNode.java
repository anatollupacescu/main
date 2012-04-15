package core.node;

import java.util.logging.Level;
import java.util.logging.Logger;

import me.prettyprint.hector.api.mutation.Mutator;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;
import core.datastore.Cassandra;
import core.misc.Const;
import core.model.Message;
import core.model.XMLMessage;

public class PushNode extends Node {

	private final static Logger logger = Logger.getLogger(PushNode.class.getName());
	
	public PushNode(String ... args) {
		super();
	}

	@Override
	public String execute(Message obj) {
		
		Document document = ((XMLMessage)obj).getDocument();
		
		if(pushData(document)) return success;
		
		return error;
	}

	
	private boolean pushData(Document document) {
		
		Nodes nodes = document.query(Const.PERSIST);
		Cassandra ds = Cassandra.getInstance();
		Mutator<String> m = ds.getMutator();
		
		for (int i = 0; i < nodes.size(); i++) {

			Element node = (Element) nodes.get(i);
			Elements elements = node.getChildElements();

			String type = node.getLocalName();
			String key = node.getAttributeValue(Const.KEY);
			
			ds.addInsertion(m, key, Const.TYPE, type);
			
			for (int j = 0; j < elements.size(); j++) {

				Element element = elements.get(j);

				String column = element.getLocalName();
				String value = element.getValue();

				ds.addInsertion(m, key, column, value);
				
				logger.log(Level.INFO, "inserted key " + key + " column " + column +" value " + value);
			}
		}
		
		return false;
		
	}
}
