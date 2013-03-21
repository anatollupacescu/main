package my.nosql.httpclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;

import my.nosql.datastore.CassandraDaoFactory;
import my.nosql.datastore.CassandraDatastoreService;
import my.nosql.datastore.exception.DatastoreException;
import my.nosql.datastore.model.Entity;
import my.nosql.datastore.model.Query;
import my.nosql.datastore.model.QueryCondition;
import my.nosql.datastore.model.QueryOperator;


public class Simple extends MyServlet {

	private static final long serialVersionUID = 1L;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		String uri = request.getRequestURI();
		
		if(uri == null) {
			out.println("Malformed command");
			return;
		}
		
		String key = uri.substring(appNameLength + 4);
		String operation = uri.substring(appNameLength, appNameLength + 3);
		
		if(key == null || key.length() == 0) {
			out.println("Malformed key");
			return;
		}
		
		if(operation == null || operation.length() == 0) {
			out.println("Malformed operation" );
			return;
		}
		
		try {
			
			Op op = Op.valueOf(operation);

			Map<String,String> map = requestToMap(request);
			
			switch (op) {
			case put:
				put(key, map);
				break;
			case get:
				get(out, key, map);
				break;
			case src:
				src(out, key, map);
				break;
			case del:
				del(key, map);
				break;
			default:
				break;
			}
			
		} catch (Exception ex) {
			out.println("Could not execute request: " + ex.getMessage());
		}
	}

	private void del(String key, Map<String, String> map) throws DatastoreException {
		CassandraDatastoreService datastoreService = new CassandraDatastoreService();
		datastoreService.setCassandraDaoFactory(new CassandraDaoFactory());
		
		Entity entity = new Entity(key, null, map);
		datastoreService.delete(new Entity[] { entity });
	}

	private void get(PrintWriter out, String key, Map<String,String> map) throws XMLStreamException, DatastoreException {
		
		Set<String> columns = map.keySet();
		String[] columnArray = new String[columns.size()];
		
		int i = 0;
		for(String col : columns) {
			columnArray[i++] = col;
		}
		
		CassandraDatastoreService datastoreService = new CassandraDatastoreService();
		datastoreService.setCassandraDaoFactory(new CassandraDaoFactory());
		
		Query query = new Query(key, null, columnArray);
		Entity[] entityArray = datastoreService.pull( new Query[] { query });
		
		if(entityArray.length > 0 && entityArray[0].getType() != null) {
			String xml = marshallEntity(entityArray[0]);
			out.println(xml);
		}
	}
	
	private void put(String keyAndType, Map<String,String> map) throws DatastoreException {
		
		CassandraDatastoreService datastoreService = new CassandraDatastoreService();
		datastoreService.setCassandraDaoFactory(new CassandraDaoFactory());
		
		String key = null;
		String type = null;
		
		int pos = keyAndType.indexOf("/");
		
		if(pos > 0) {
			key = keyAndType.substring(0, pos);
			type = keyAndType.substring(pos+1);
		}
		
		if(key != null && type !=null) { //insert
			datastoreService.push(new Entity[] { new Entity(key, type, map)});
		} else { //update
			datastoreService.push(new Entity[] { new Entity(keyAndType, null, map)});
		}
	}
	
	private void src(PrintWriter out, String type, Map<String,String> map) throws DatastoreException, XMLStreamException {
		
		Set<String> columns = map.keySet();
		List<String> columnList = new ArrayList<String>(columns.size());
		List<QueryCondition> queryConditionList = new ArrayList<QueryCondition>();
		
		for(String col : columns) {
			if(col.startsWith(":")) {
				String value = map.get(col);
				int pos = value.indexOf(":", 2);
				String operator = value.substring(0, pos);
				QueryOperator qo = QueryOperator.valueOf(operator);
				QueryCondition qc = new QueryCondition(col.substring(1), qo, value.substring(pos + 1));
				queryConditionList.add(qc);
			} else 
			columnList.add(col);
		}
		
		CassandraDatastoreService datastoreService = new CassandraDatastoreService();
		datastoreService.setCassandraDaoFactory(new CassandraDaoFactory());
		
		String[] columnArray = new String[columnList.size()];
		QueryCondition[] conditionArray = new QueryCondition[queryConditionList.size()];
		Query query = new Query(type, columnList.toArray(columnArray), queryConditionList.toArray(conditionArray));
		Entity[] entityArray = datastoreService.pull( new Query[] { query });
		
		if(entityArray.length > 0 && entityArray[0].getType() != null) {
			String xml = marshallEntity(entityArray[0]);
			out.println(xml);
		}
	}
}
