package core.node;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import me.prettyprint.hector.api.mutation.Mutator;
import net.trivial.wf.iface.Action;
import net.trivial.wf.iface.Message;
import core.datastore.impl.Datastore;
import core.model.message.XMLMessage;
import core.node.parent.Node;

public class PushNode extends Node implements Action{

	private final String KEY = "key";
	private final String TYPE="type";
	
	public PushNode(String ... args) {
		super();
	}

	@Override
	public String execute(Message obj, Object... arg1) {
		
		XMLMessage xmlMessage = (XMLMessage)obj;
		String message = xmlMessage.getText();
		
		if(message == null) return error;
		
		if(pushData(message)) return success;
		
		return error;
	}

	private boolean pushData(String text) {
		
		try {
			
			String xml = executeQuery(text);
			
			XMLInputFactory xif = XMLInputFactory.newInstance();
			InputStream stream = new ByteArrayInputStream(xml.getBytes());
			XMLStreamReader reader = xif.createXMLStreamReader(stream);
			
			String name = null;
			String key = null;
			
			Datastore ds = Datastore.getInstance();
			Mutator<String> m = ds.getMutator();
			
			while (reader.hasNext()) {
				
				int eventType = reader.next();
				
				if (eventType == XMLStreamReader.START_ELEMENT) {

					if(reader.getAttributeCount() > 0 && KEY.equals(reader.getAttributeName(0).toString())) {
						key = reader.getAttributeValue(0);
						ds.addInsertion(m, key, TYPE, reader.getLocalName());
					}

					name = reader.getLocalName();
					
				} else if (eventType == XMLStreamReader.END_ELEMENT) {
					name = null;
				} else if (eventType == XMLStreamReader.CHARACTERS && name != null && !TYPE.equals(name)) {
					ds.addInsertion(m, key, name, reader.getText());
				}
			}
			
			m.execute();
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		return false;
	}
}
