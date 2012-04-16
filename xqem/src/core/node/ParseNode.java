package core.node;

import java.util.logging.Level;
import java.util.logging.Logger;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import core.misc.XMLBuilder;
import core.model.Message;
import core.model.XMLMessage;

public class ParseNode extends GenericNode {
	
	private final static Logger logger = Logger.getLogger(ParseNode.class.getName());
	
	public ParseNode(String ... args) {
		super(args);
	}

	@Override
	public String execute(Message obj) {

		Document document = ((XMLMessage)obj).getDocument();
		
		try {
			String parsedMessage = executeQuery(document.toXML());
			Element root = document.getRootElement();
			Document doc = XMLBuilder.build(parsedMessage);
			Node response = doc.getChild(0);
			for(int i=0; i < response.getChildCount(); i++) {
				Node node = response.getChild(i).copy();
				root.appendChild(node);
			}
			return success;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Could not parse document", e);
		}
		
		return error;
	}
}
