package net.xqwf.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import nu.xom.Document;
import nu.xom.Serializer;

public class XMLHelper {

	public static String format(Document document) {
		OutputStream result = new ByteArrayOutputStream();
        Serializer serializer;
		try {
			serializer = new Serializer(result, "UTF-8");
			serializer.setIndent(4);
	        serializer.setMaxLength(64);
	        serializer.setPreserveBaseURI(true);
	        serializer.write(document);
	        serializer.flush();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return result.toString();
	}
	
	public static String format(String input) {
		Document document = DocumentHelper.createDocumentFromString(input);
		return format(document);
	}
}
