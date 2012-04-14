package core.model.message;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.trivial.wf.iface.Message;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;

public class XMLMessage extends Message {

	private final static Logger logger = Logger.getLogger(XMLMessage.class.getName());
	private final static Builder parser = new Builder();
	
	private final Document document;
	
	public XMLMessage(String state, String xml) {
		super(state);
		Document doc = null;
		try {
			doc = parser.build(xml, null);
		} catch (ParsingException ex) {
			logger.log(Level.SEVERE, "Could not parse xml", ex);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Could not read xml", ex);
		}
		document = doc;
	}

	public Document getDocument() {
		return document;
	}
}
