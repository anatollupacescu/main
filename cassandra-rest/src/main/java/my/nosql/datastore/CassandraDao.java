package my.nosql.datastore;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import me.prettyprint.cassandra.model.IndexedSlicesQuery;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.ColumnSliceIterator;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.ColumnQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.SliceQuery;
import my.nosql.datastore.model.QueryCondition;


public class CassandraDao {

	private final StringSerializer ss = StringSerializer.get();
	private final String columnFamilyName;
	private final Keyspace keyspace;
	
	public CassandraDao(final String columnFamily, Keyspace keyspace) {
		columnFamilyName = columnFamily;
		this.keyspace = keyspace;
	}
	
	public String get(final String key, final String columnName) throws HectorException {
		ColumnQuery<String, String, String> q = HFactory.createColumnQuery(keyspace, ss, ss, ss);
		QueryResult<HColumn<String, String>> r = q.setKey(key).setName(columnName).setColumnFamily(columnFamilyName).execute();
		HColumn<String, String> c = r.get();
		return c != null ? c.getValue() : null;
	}

	public Map<String, String> get(String key) {
		Map<String, String> map = new HashMap<String, String>();
		SliceQuery<String, String, String> query = HFactory.createSliceQuery(keyspace, ss, ss, ss).setKey(key).setColumnFamily(columnFamilyName);
		ColumnSliceIterator<String, String, String> iterator = new ColumnSliceIterator<String, String, String>(query, null, "\uFFFF", false);
		while (iterator.hasNext()) {
			HColumn<String, String> item = iterator.next();
			map.put(item.getName(), item.getValue());
		}
		return map;
	}

	public void put(String key, Map<String, String> columnValues) {
		Mutator<String> m = HFactory.createMutator(keyspace, ss);
		for (Map.Entry<String, String> keyValue : columnValues.entrySet()) {
			m.addInsertion(key, columnFamilyName, HFactory.createColumn(keyValue.getKey(), keyValue.getValue(), keyspace.createClock(), ss, ss));
		}
		m.execute();
	}

	public void put(String key, String column, String value) {
		Mutator<String> m = HFactory.createMutator(keyspace, ss);
		m.addInsertion(key, columnFamilyName, HFactory.createColumn(column, value, keyspace.createClock(), ss, ss));
		m.execute();
	}

	public void del(String key, String... columns) {
		Mutator<String> m = HFactory.createMutator(keyspace, ss);
		for (String column : columns) {
			m.addDeletion(key, columnFamilyName, column, ss);
		}
		m.execute();
	}
	
	public void del(String key, Set<String> columns) {
		Mutator<String> m = HFactory.createMutator(keyspace, ss);
		for (String column : columns) {
			m.addDeletion(key, columnFamilyName, column, ss);
		}
		m.execute();
	}
	
	/**
	 * searches keys of the entities that corespond given criteria
	 * 
	 * @param type
	 * @param conditions
	 * @return String array with all keys found
	 */
	public String[] search(String type, QueryCondition[] conditions) {
		
		if(type == null || conditions == null) return null;
		
		IndexedSlicesQuery<String, String, String> indexedSlicesQuery = HFactory.createIndexedSlicesQuery(keyspace, StringSerializer.get(), StringSerializer.get(), StringSerializer.get());
		indexedSlicesQuery.setColumnFamily(columnFamilyName);
		indexedSlicesQuery.setStartKey("");
		indexedSlicesQuery.setReturnKeysOnly();
		indexedSlicesQuery.addEqualsExpression(Const.COLUMN_NAME_TYPE, type);
		
		for (QueryCondition qc : conditions) {

			switch (qc.operator) {
			case EQ:
				indexedSlicesQuery.addEqualsExpression(qc.column, qc.value);
				break;
			case GT:
				indexedSlicesQuery.addGtExpression(qc.column, qc.value);
				break;
			case GTE:
				indexedSlicesQuery.addGteExpression(qc.column, qc.value);
				break;
			case LT:
				indexedSlicesQuery.addLtExpression(qc.column, qc.value);
				break;
			case LTE:
				indexedSlicesQuery.addLteExpression(qc.column, qc.value);
				break;
			}
		}
		
		QueryResult<OrderedRows<String, String, String>> result = indexedSlicesQuery.execute();
		OrderedRows<String, String, String> rows = result.get();
		
		String[] keys = new String[rows.getCount()];
		int index = 0;
		
		for (Row<String, String, String> r : rows) {
			keys[index++] = r.getKey();
		}
		
		return keys;
	}
}
