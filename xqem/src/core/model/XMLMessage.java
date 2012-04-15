package core.model;

import nu.xom.Document;
import core.misc.XMLBuilder;

public class XMLMessage extends Message {

	private final Document document;
	
	public XMLMessage(String state, String xml) {
		super(state);
		document = XMLBuilder.build(xml);
	}

	public Document getDocument() {
		return document;
	}
}
