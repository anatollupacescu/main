package core.node;

import java.util.logging.Level;
import java.util.logging.Logger;

import nu.xom.Document;
import core.misc.XMLBuilder;
import core.model.Message;
import core.model.XMLMessage;

public class ParseNode extends Node {
	
	private final static Logger logger = Logger.getLogger(ParseNode.class.getName());
	
	public ParseNode(String ... args) {
		super(args);
	}

	@Override
	public String execute(Message obj) {

		Document document = ((XMLMessage)obj).getDocument();
		
		try {
			String parsedMessage = executeQuery(document.toXML());
			Document doc = XMLBuilder.build(parsedMessage);
			document.appendChild(doc);
			return success;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Could not parse document", e);
		}
		
		return error;
	}
}
