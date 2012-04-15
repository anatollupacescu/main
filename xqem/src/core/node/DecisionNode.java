package core.node;

import nu.xom.Document;
import core.model.Message;
import core.model.XMLMessage;

public class DecisionNode extends Node {

	public DecisionNode(String ... args) {
		super(args);
	}
	
	@Override
	public String execute(Message obj) {
		
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
