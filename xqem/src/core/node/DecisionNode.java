package core.node;

import java.util.logging.Level;
import java.util.logging.Logger;

import nu.xom.Document;
import core.model.Message;
import core.model.XMLMessage;

public class DecisionNode extends Node {

	private final static Logger logger = Logger.getLogger(DecisionNode.class.getName());
	
	public DecisionNode(String ... args) {
		super(args);
	}
	
	@Override
	public String execute(Message obj) {
		
		if(obj == null) return error;
		
		Document document = ((XMLMessage)obj).getDocument();
		
		if(document == null) return error;
		
		try {
			String returnCode = executeQuery(document.toXML());
			logger.log(Level.INFO, "Returned code is " + returnCode);
			return success;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Could not compute return code", e);
		}
		
		return error;
	}
}
