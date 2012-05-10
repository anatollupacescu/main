package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.xquery.XQException;

import my.XQWFDocument;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.apache.commons.io.IOUtils;

import core.helper.DocumentHelper;

public class Test {

	private final static String fileName = "c:\\Users\\Anatol\\workspace\\xqwf\\src\\test\\resources\\test.xml";
	
	public static void main(String[] args) throws XQException, ValidityException, ParsingException, IOException {
		
		XQWFDocument document = new XQWFDocument();
		
		System.exit(1);
		
		String xml = readFile(fileName);
		Document doc = DocumentHelper.createDocumentFromString(xml);
	}
	
	public static String readFile(String fileName) {
		
		InputStream in = null;
		
		try {
			in = new FileInputStream(fileName);
			return IOUtils.toString(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
		}
		return null;
	}
}
