package core.node;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQSequence;

import org.apache.commons.io.IOUtils;

import me.prettyprint.hector.api.mutation.Mutator;
import net.trivial.wf.iface.Action;
import net.trivial.wf.iface.Message;
import ch.ethz.mxquery.xqj.MXQueryXQDataSource;

import com.thoughtworks.xstream.XStream;

import core.datastore.impl.Datastore;
import core.datastore.query.Query;
import core.model.datapull.Condition;
import core.model.datapull.Datarequest;
import core.model.datapull.Entity;
import core.model.message.XMLMessage;

public class ActionNode implements Action{

	private final String KEY = "key";
	private final String TYPE="type";
	
	static enum op { EQ, LTE, LT, GTE, GT };
	
	private final String pullQuery;
	private final String parseQuery;
	private final String pushQuery;
	private final String decisionQuery;
	
	public ActionNode(String ... args) {
		super();
		this.pullQuery = validArgument(0, args);
		this.parseQuery = validArgument(1, args);
		this.pushQuery = validArgument(2, args);
		this.decisionQuery = validArgument(3, args);
	}

	private static String validArgument(int index, String ... args) {
		if(args != null && args.length > index && !args[index].isEmpty()) {
			InputStream in = null;
			String content = null;

			try {
				in = new FileInputStream(args[index]);
				content = IOUtils.toString(in);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(in);
			}
			return content;
		}
		return null;
	}
	
	@Override
	public String execute(Message obj, Object... arg1) {
		
		XMLMessage xmlMessage = (XMLMessage)obj;
		String m = xmlMessage.getText();
		
		if(m == null) return "error";
		
		StringBuilder text = new StringBuilder(m);
		
		if(pullQuery != null) {
			String datarequest = null;
			try {
				datarequest = executeQuery(xmlMessage.getText(), pullQuery);
			} catch (XQException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			String data = pullData(datarequest); 
			text.append(data);
			xmlMessage.setText(text.toString());
		}
		
		if(parseQuery != null) {
			try {
				xmlMessage.setText(parseData(text.toString()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(pushQuery != null) {
			pushData(xmlMessage.getText());
		}
		
		return decide(xmlMessage.getText());
	}

	private void pushData(String text) {
		
		try {
			
			String xml = executeQuery(text, pushQuery);
			
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
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
	}

	private String executeQuery(String text, String query) throws XQException, IOException {
		
		XQDataSource xqdatasource = new MXQueryXQDataSource();
		XQConnection xqconnection = xqdatasource.getConnection();

		StringBuilder q = new StringBuilder();
		q.append("let $document := \n");
		q.append(text);
		q.append("\n");
		q.append(query);
		
		XQExpression xqexpression = xqconnection.createExpression();
		XQSequence xqsequence = xqexpression.executeQuery(q.toString());
		
		OutputStream result = new ByteArrayOutputStream(); 
		xqsequence.writeSequence(result, null);
		
		return result.toString().trim();
	}

	private String parseData(String text) throws XQException, IOException {
		return executeQuery(text, parseQuery);
	}

	private String pullData(String text) {
		
		if(text == null) return null;
		
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
	
	private String decide(String text) {
		try {
			return executeQuery(text, decisionQuery);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "error";
	}
}
