package core.node;

import java.io.IOException;

import javax.xml.xquery.XQException;

import net.trivial.wf.iface.Action;
import net.trivial.wf.iface.Message;

import com.thoughtworks.xstream.XStream;

import core.datastore.impl.Datastore;
import core.datastore.pull.Query;
import core.datastore.pull.model.Condition;
import core.datastore.pull.model.Datarequest;
import core.datastore.pull.model.Entity;
import core.model.message.XMLMessage;
import core.node.parent.Node;

public class PullNode extends Node implements Action{

	private static enum op { EQ, LTE, LT, GTE, GT };
	
	public PullNode(String ... args) {
		super(args);
	}

	@Override
	public String execute(Message obj, Object... arg1) {

		XMLMessage xmlMessage = (XMLMessage) obj;
		String message = xmlMessage.getText();

		if (message == null)
			return error;

		String datarequest = null;
		try {
			datarequest = executeQuery(message);
			String pulledData = pullData(datarequest);
			xmlMessage.setText(message + pulledData);

			return success;
		} catch (XQException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return error;

	}

	private String pullData(String text) {
		
		XStream xstream = getXStreamObject();
		Datarequest datarequest = (Datarequest)xstream.fromXML(text);
		Query[] queries = datarequestObjectToQueryArray(datarequest);
		
		Datastore ds = Datastore.getInstance();
		
		String xml = null;
		
		try {
			xml = ds.queryXML(queries);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return xml;
	}

	private Query[] datarequestObjectToQueryArray(Datarequest datarequest) {

		Query[] queries = new Query[datarequest.entity.length];
		
		int i = 0;
		
		for(Entity entity : datarequest.entity) {
			
			Query query = new Query(entity.name);
			
			for(Condition column : entity.conditions) {
				
				op operation = op.valueOf(column.expression);
				
				switch (operation) {
				case EQ:
					query.eq(column.column, column.value);
					break;
				case GT:
					query.gt(column.column, column.value);
					break;
				case GTE:
					query.gte(column.column, column.value);
					break;
				case LT:
					query.lt(column.column, column.value);
					break;
				case LTE:
					query.lte(column.column, column.value);
					break;
				}
			}
			
			query.columns(entity.columns);
			
			queries[i++] = query;
		}
		
		return queries;
	}

	private XStream getXStreamObject() {
		
		XStream xstream = new XStream();
		
        xstream.alias("datarequest", Datarequest.class);
        xstream.alias("entity", Entity.class);
        xstream.alias("condition", Condition.class);
        
        return xstream;
	}
	
}
