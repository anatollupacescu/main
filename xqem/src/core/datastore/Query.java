package core.datastore;

import me.prettyprint.cassandra.model.IndexedSlicesQuery;
import me.prettyprint.hector.api.query.MultigetSliceQuery;

public class Query {
	
	public final IndexedSlicesQuery<String, String, String> indexedQuery;
	public final MultigetSliceQuery<String, String, String> multigetQuery;
	
	public final String type;
	public final String[] keys;
	public final String[] columns;
	
	public Query(String t, String[] k, String[] c) {
		
		type = t;
		keys = k;
		columns = c;
		
		Cassandra cassandra = Cassandra.getInstance();
		
		if(keys == null) {
			indexedQuery = cassandra.getIndexedQuery(t);
			multigetQuery = null;
			if (columns != null) {
				indexedQuery.setColumnNames(columns);
			} else {
				indexedQuery.setRange("", "", false, 100);
			}
		} else {
			indexedQuery = null;
			multigetQuery = cassandra.getMultigetQuery(t, keys);
			if (columns != null) {
				multigetQuery.setColumnNames(c);
			}else{
				multigetQuery.setRange("", "", false, 100);
			}
		}
	}
	
	public void eq(String columnName, String columnValue) {
		indexedQuery.addEqualsExpression(columnName, columnValue);
	}

	public void gt(String columnName, String columnValue) {
		indexedQuery.addGtExpression(columnName, columnValue);
	}
	
	public void gte(String columnName, String columnValue) {
		indexedQuery.addGteExpression(columnName, columnValue);
	}
	
	public void lt(String columnName, String columnValue) {
		indexedQuery.addLtExpression(columnName, columnValue);
	}
	
	public void lte(String columnName, String columnValue) {
		indexedQuery.addLteExpression(columnName, columnValue);
	}
}
