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
import my.nosql.datastore.exception.EntityIsBusyException;
import my.nosql.datastore.model.Entity;
import my.nosql.datastore.model.Query;
import my.nosql.datastore.model.QueryCondition;
import my.nosql.datastore.model.QueryOperator;


public class Transactional extends MyServlet {

	private static final long serialVersionUID = 1L;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		String uri = request.getRequestURI();
		
		if(uri == null) {
			out.println("Malformed command");
			return;
		}
		
		String key = uri.substring(appNameLength + 5);
		String operation = uri.substring(appNameLength, appNameLength + 4);
		
		if(key == null || key.length() == 0) {
			out.println("Malformed key" );
			return;
		}
		
		if(operation == null || operation.length() == 0) {
			out.println("Malformed operation" );
			return;
		}
		
		try {
			
			Op op = Op.valueOf(operation);

			Map<String,String> map = null;
			String[] uriArr = null;
			
			switch (op) {
			
			case tget:
				map = requestToMap(request);
				uriArr = parseURI(key);
				get(out, uriArr[0], uriArr[1], map);
				break;
			
			case tput:
				map = requestToMap(request);
				uriArr = parseURI(key);
				put(uriArr[0], uriArr[1], map);
				break;
				
			case tsrc:
				map = requestToMap(request);
				uriArr = parseURI(key);
				src(out, uriArr[0], uriArr[1], map);
				break;
				
			case tdel:
				map = requestToMap(request);
				uriArr = parseURI(key);
				del(uriArr[0], uriArr[1], map);
				break;
				
			case tbeg:
				beg(key);
				break;
				
			case tcom:
				com(key);
				break;
				
			case trol:
				rol(key);
				break;
				
			default:
				break;
			}
		} catch (Exception ex) {
			out.println("Could not execute request: " + ex.getMessage());
		}
		out.flush();
		out.close();
	}

	private void rol(String key) throws DatastoreException {
		
		CassandraDatastoreService datastoreService = new CassandraDatastoreService();
		datastoreService.setCassandraDaoFactory(new CassandraDaoFactory());
		
		datastoreService.rollbackTransaction(key);
	}

	private void com(String key) throws DatastoreException {
		
		CassandraDatastoreService datastoreService = new CassandraDatastoreService();
		datastoreService.setCassandraDaoFactory(new CassandraDaoFactory());
		
		datastoreService.commitTransaction(key);
	}

	private void beg(String key) throws DatastoreException {
		
		CassandraDatastoreService datastoreService = new CassandraDatastoreService();
		datastoreService.setCassandraDaoFactory(new CassandraDaoFactory());
		
		datastoreService.beginTransaction(key);
	}

	private void del(String transaction, String key, Map<String, String> map) throws EntityIsBusyException {
		
		CassandraDatastoreService datastoreService = new CassandraDatastoreService();
		datastoreService.setCassandraDaoFactory(new CassandraDaoFactory());
		
		datastoreService.delete(new Entity[] { new Entity(key, null, map)} , transaction);
	}

	private void get(PrintWriter out, String transaction, String key, Map<String,String> map) throws DatastoreException, XMLStreamException {

		CassandraDatastoreService datastoreService = new CassandraDatastoreService();
		datastoreService.setCassandraDaoFactory(new CassandraDaoFactory());
		
		Query query = new Query(key, map.keySet());
		Entity[] entities = datastoreService.pull(new Query[] { query }, transaction);
		
		if(entities.length > 0 && entities[0].getType() != null) {
			String xml = marshallEntity(entities[0]);
			out.println(xml);
		}
	}
	
	private void put(String transaction, String keyAndType, Map<String,String> map) throws DatastoreException {
		
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
			datastoreService.push(new Entity[] { new Entity(key, type, map)}, transaction);
		} else { //update
			datastoreService.push(new Entity[] { new Entity(keyAndType, null, map)}, transaction);
		}
	}
	
	private void src(PrintWriter out, String transaction, String type, Map<String,String> map) throws DatastoreException, XMLStreamException {
		
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
		Entity[] entityArray = datastoreService.pull( new Query[] { query }, transaction);
		
		if(entityArray.length > 0 && entityArray[0].getType() != null) {
			String xml = marshallEntity(entityArray[0]);
			out.println(xml);
		}
	}
}
