package net.xqwf.misc;

import java.io.IOException;
import java.io.ObjectInputStream;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;

public class DocumentHelper {

	private final static Builder parser = new Builder();
	
	public static Document createEmptyDocument() {
		Element element = new Element(Const.REQUEST);
		return new nu.xom.Document(element);
	}
	
	public static Document createDocumentFromString(String xml) {
		try {
			return parser.build(xml, null);
		} catch (ParsingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Document createDocumentFromStream(ObjectInputStream in) {
		try {
			return parser.build(in);
		} catch (ParsingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
