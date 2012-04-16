package core.node;

import java.util.logging.Level;
import java.util.logging.Logger;

import nu.xom.Document;
import core.misc.XMLBuilder;
import core.model.Message;
import core.model.XMLMessage;

public class DecisionNode extends GenericNode {

	private final static Logger logger = Logger.getLogger(DecisionNode.class.getName());
	
	public DecisionNode(String ... args) {
		super(args);
	}
	
	@Override
	public String execute(Message obj) {
		
		Document document = ((XMLMessage)obj).getDocument();
		
		try {
			String returnCode = executeQuery(document.toXML());
			Document doc = XMLBuilder.build(returnCode);
			logger.log(Level.INFO, "Returned data is " + returnCode);
			return doc.query("/response").get(0).getValue();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Could not compute return code", e);
		}
		
		return error;
	}
}
