package my.test;

import java.io.FileInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;

import core.model.XMLMessage;
import core.wf.Workflow;

public class ActionTest extends TestCase {

	public static void main(String[] args) throws Exception {
		Workflow dwf = new Workflow("src\\config.properties");
		
		XMLMessage message = new XMLMessage(dwf.initialState, readFile("src\\resources\\request.xml"));
		dwf.doProcess(message);
		dwf.doProcess(message);
	}
	
	static String readFile(String filename) {
		InputStream in = null;
		try {
			in = new FileInputStream(filename);
			return IOUtils.toString(in);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
		}
		return null;
	}
	
}
