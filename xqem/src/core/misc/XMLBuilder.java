package core.misc;

import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;

public class XMLBuilder {

	private final static Builder parser = new Builder();
	
	public static Document build(String xml) {
		try {
			return parser.build(xml, null);
		} catch (ParsingException | IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
