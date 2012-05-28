package net.xqwf.helper;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import net.xqwf.Const;
import nu.xom.Document;
import nu.xom.Serializer;

import org.apache.commons.io.IOUtils;

public class MiscHelper {

	public static String readFile(String fileName) {
		InputStream in = null;
		String fileContents = null;
		try {
			in = new FileInputStream(fileName);
			fileContents = Const.XQUERY_PREFIX + IOUtils.toString(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
		}
		return fileContents;
	}
	
	public static String formatDocument(Document document) {
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
}
