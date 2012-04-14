package core.node;

import net.trivial.wf.iface.Action;
import net.trivial.wf.iface.Message;
import nu.xom.Document;
import core.model.message.XMLMessage;
import core.node.parent.Node;

public class ParseNode extends Node implements Action{
	
	public ParseNode(String ... args) {
		super(args);
	}

	@Override
	public String execute(Message obj, Object... arg1) {

		if(obj == null) return error;
		
		Document document = ((XMLMessage)obj).getDocument();
		
		if(document == null) return error;

		try {
			String parsedMessage = executeQuery(document.toXML());
//			((XMLMessage) obj).setText(parsedMessage);
			return success;
		} catch (Exception e) {
			e.printStackTrace();
			return error;
		}
	}
}
