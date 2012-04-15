package my.test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;

import core.model.message.XMLMessage;
import core.wf.Workflow;

public class ActionTest extends TestCase {

	private final static Logger logger = Logger.getLogger(ActionTest.class.getName());
	
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
