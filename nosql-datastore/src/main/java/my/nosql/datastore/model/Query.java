package my.nosql.datastore.model;

import java.util.Arrays;
import java.util.Set;


public class Query {

	private String key = null;
	public final String type;
	public final String[] columns;
	public final QueryCondition[] conditions;
	private String transaction;
	
	public Query(String key, String type, String[] columns) {
		this.key = key;
		this.type = type;
		this.columns = columns;
		this.conditions = null;
	}
	
	public Query(String type, String[] columns, QueryCondition[] conditions) {
		this.type = type;
		this.columns = columns;
		this.conditions = conditions;
	}
	
	public Query(String key, Set<String> keySet) {
		this.key = key;
		String[] columns = new String[keySet.size()];
		int i = 0;
		for(String c : keySet) {
			columns[i++] = c;
		}
		this.columns = columns;
		this.type = null;
		this.conditions = null;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getType() {
		return type;
	}
	
	public String getTransaction() {
		return transaction;
	}

	public void setTransaction(String transaction) {
		this.transaction = transaction;
	}
	
	@Override
	public String toString() {
		return "Query [key=" + key + ", columns=" + Arrays.toString(columns) + ", conditions=" + Arrays.toString(conditions) + "]";
	}
}
