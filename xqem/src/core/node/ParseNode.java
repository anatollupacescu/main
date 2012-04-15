package core.node;

import nu.xom.Document;
import core.model.Message;
import core.model.XMLMessage;

public class ParseNode extends Node {
	
	public ParseNode(String ... args) {
		super(args);
	}

	@Override
	public String execute(Message obj) {

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
