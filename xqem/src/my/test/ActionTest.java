package my.test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import junit.framework.TestCase;
import net.trivial.wf.Workflow;

import org.apache.commons.io.IOUtils;

import com.thoughtworks.xstream.XStream;

import core.datastore.pull.model.Condition;
import core.datastore.pull.model.Datarequest;
import core.datastore.pull.model.Entity;
import core.model.message.XMLMessage;
import core.node.impl.ParseNode;

public class ActionTest extends TestCase {

	private final static Logger logger = Logger.getLogger(ActionTest.class.getName());
	
	private XMLMessage getMessage() {

		InputStream in = null;
		String content = null;

		try {
			in = new FileInputStream("src\\test\\java\\request.xml");
			content = IOUtils.toString(in);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
		}

		XMLMessage m = new XMLMessage("DISPATCH");
		m.setText(content);

		return m;
	}
	
	public String getDatarequestXML() {
		Datarequest dr = new Datarequest();
		Entity e1 = new Entity();
		e1.name = "user";
		
		Condition[] cs = new Condition[1];
		
		Condition c1 = new Condition();
		c1.column = "age";
		c1.expression="GT";
		c1.value="24";
		
		Condition c2 = new Condition();
		c2.column = "name";
		c2.expression="EQ";
		c2.value="tolea";
		
		cs[0] = c1;
//		cs[1] = c2;
		
		e1.conditions = cs;
		
		e1.columns = new String[] { "name", "type" };
		
		dr.entity = new Entity[1];
		dr.entity[0] = e1;
		
		XStream xstream = new XStream();

        xstream.alias("datarequest", Datarequest.class);
        xstream.alias("entity", Entity.class);
        xstream.alias("condition", Condition.class);
        
        return xstream.toXML(dr);
	}
	
	public void notestPull() {
		
		String pull = getDatarequestXML();
		
		logger.info("pull " + pull);
		
		String parse="return $document";
		String push = "return $document//user[@action='persist']";
		String decision = "return count($document//user[@action='persist']) > 1";

		ParseNode node = new ParseNode(pull, parse, push, decision);
		
		XMLMessage m = getMessage();
		node.execute(m);
	}
	
	public void testApp() throws Exception {
		
		Workflow dwf = new Workflow("src\\test\\java\\config.properties");
		
		XMLMessage message = (XMLMessage)dwf.newModelInstance();
		message.setText(getMessage().getText());
		dwf.doProcess(message);
		dwf.doProcess(message);
	}
}
