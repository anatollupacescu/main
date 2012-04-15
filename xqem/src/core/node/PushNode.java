package core.node;

import me.prettyprint.hector.api.mutation.Mutator;
import net.trivial.wf.iface.Action;
import net.trivial.wf.iface.Message;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;
import core.datastore.Cassandra;
import core.model.message.XMLMessage;
import core.node.parent.Node;

public class PushNode extends Node implements Action{

	private final String KEY = "key";
	private final String TYPE="type";
	
	public PushNode(String ... args) {
		super();
	}

	@Override
	public String execute(Message obj, Object... arg1) {
		
		if(obj == null) return error;
		
		Document document = ((XMLMessage)obj).getDocument();
		
		if(document == null) return error;
		
		if(pushData(document)) return success;
		
		return error;
	}

	
	private boolean pushData(Document document) {
		
		Nodes nodes = document.query("/request/*[@action='persist']");
		Cassandra ds = Cassandra.getInstance();
		Mutator<String> m = ds.getMutator();
		
		for (int i = 0; i < nodes.size(); i++) {

			Element node = (Element) nodes.get(i);
			Elements elements = node.getChildElements();

			String type = node.getLocalName();
			String key = node.getAttributeValue(KEY);
			
			ds.addInsertion(m, key, TYPE, type);
			
			for (int j = 0; j < elements.size(); j++) {

				Element element = elements.get(j);

				String column = element.getLocalName();
				String value = element.getValue();

				ds.addInsertion(m, key, column, value);
			}
		}
		
		return false;
		
	}
}
