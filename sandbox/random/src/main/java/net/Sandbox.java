package net;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.parser.DynamicParser;
import net.parser.Predefined;

public class Sandbox {

	private final static Logger log = Logger.getAnonymousLogger();
	private final static String content = "{\"menu\" : {\"header\": \"SVG Viewer\"}}";
	
	public DynamicParser myParser() {
		DynamicParser key = DynamicParser.newBuilder().start(Predefined.STRING).end(":").build();
		DynamicParser keyValue = DynamicParser.newBuilder().start(key).end(Predefined.STRING).build();
		return DynamicParser.newBuilder().start("{").many(keyValue).end("}").build();
	}
	
	public static void main(String[] args) throws IOException {
		Sandbox sandbox = new Sandbox();
		DynamicParser parser = sandbox.myParser();
		boolean isOk = parser.parse(content);
		log.log(Level.INFO, "Response is {0}", new Object[] { isOk });
	}
}
