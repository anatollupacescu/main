package core.node;

import net.trivial.wf.iface.Action;
import net.trivial.wf.iface.Message;
import nu.xom.Document;
import core.model.message.XMLMessage;
import core.node.parent.Node;

public class DecisionNode extends Node implements Action {

	public DecisionNode(String ... args) {
		super(args);
	}
	
	@Override
	public String execute(Message obj, Object... arg1) {
		
		if(obj == null) return error;
		
		Document document = ((XMLMessage)obj).getDocument();
		
		if(document == null) return error;
		
		try {
			return executeQuery(document.toXML());
		} catch (Exception e) {
			e.printStackTrace();
			return error;
		}
	}
}
