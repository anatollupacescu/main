package core.datastore.pull;

import core.datastore.impl.Datastore;
import me.prettyprint.cassandra.model.IndexedSlicesQuery;

public class Query {
	
	private final IndexedSlicesQuery<String, String, String> query;
	private final String type;
	
	public Query(String t) {
		type = t;
		Datastore ds = Datastore.getInstance();
		query = ds.getQuery(t);
	}
	
	public void eq(String columnName, String columnValue) {
		query.addEqualsExpression(columnName, columnValue);
	}

	public void gt(String columnName, String columnValue) {
		query.addGtExpression(columnName, columnValue);
		
	}
	
	public void gte(String columnName, String columnValue) {
		query.addGteExpression(columnName, columnValue);
	}
	
	public void lt(String columnName, String columnValue) {
		query.addLtExpression(columnName, columnValue);
	}
	
	public void lte(String columnName, String columnValue) {
		query.addLteExpression(columnName, columnValue);
	}
	
	public void columns(String...cols) {
		query.setColumnNames(cols);
	}
	
	public IndexedSlicesQuery<String, String, String> getQuery() {
		return query;
	}

	public String getType() {
		return type;
	}
}
