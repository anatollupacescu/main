package com.service.datastore.thrift;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.IndexClause;
import org.apache.cassandra.thrift.IndexExpression;
import org.apache.cassandra.thrift.IndexOperator;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.KeyRange;
import org.apache.cassandra.thrift.KeySlice;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;

import com.service.IDatastore;
import com.util.Const;

public class Datastore extends DatastoreBase implements IDatastore {

	private static final class holder {
		private static final Datastore datastore = new Datastore();
	}
	
	public static final Datastore getInstance() {
		return holder.datastore;
	}
	
	private void insert(String table, String column, String key, String value, TTransport tr) throws InvalidRequestException, TException, UnsupportedEncodingException, UnavailableException, TimedOutException {
		Cassandra.Client client = getClient();
		client.set_keyspace(Const.KEYSPACE);
		
		Column nameColumn = new Column(toByteBuffer(column));
		nameColumn.setValue(toByteBuffer(value));
		
		long timestamp = System.currentTimeMillis();
		nameColumn.setTimestamp(timestamp);
		
		ColumnParent parent = new ColumnParent(table);
		client.insert(toByteBuffer(key), parent, nameColumn, ConsistencyLevel.ONE);
	}
	
	
	public void store(String table, String column, String key, String value) {
		
		TTransport tr = getTransport();
		
		try {
			
			tr.open();

			insert(table, column, key, value, tr);
			
			tr.flush();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			tr.close();
		}
	}

	
	public <K> void store(String table, String key, Map<String, String> data) {

		TTransport tr = getTransport();

		try {

			tr.open();

			Set<String> keyset = data.keySet();

			for (String k : keyset) {
				String value = (String) data.get(k);
				insert(table, k, key, value, tr);
			}

			tr.flush();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tr.close();
		}
	}

	
	public <K> void store(String table, Map<String, Map<String, String>> keyColValues) {
		
		TTransport tr = getTransport();

		try {

			tr.open();

			Set<Entry<String, Map<String, String>>> keyset = keyColValues.entrySet();

			for (Entry<String, Map<String, String>> k : keyset) {
				String key = k.getKey();
				Set<Entry<String, String>> colValues = k.getValue().entrySet();
				for(Entry<String, String> cv : colValues) {
					insert(table, cv.getKey(), key, cv.getValue(), tr);
				}
			}

			tr.flush();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tr.close();
		}
	}
	
	
	public void delete(String table, String column, String[] keys) {
		// TODO Auto-generated method stub
		
	}

	
	public String get(String table, String column, String key) {
		
		List<String> columns = new ArrayList<String>();
		columns.add(column);
		
		try {
			
			List<KeySlice> list = retrieve(table, key, columns, null, MAX_INDEX_COLS);
			
			for(KeySlice ks : list) {
				
				for (ColumnOrSuperColumn c : ks.getColumns()) {
					if(column.equals(c.column.name)) return toString(c.column.value);
				}

			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	
	public Map<String, String> get(String table, String key) {
		
		Map<String, String> row = new HashMap<String, String>();
		
		try {
			
			List<KeySlice> list = retrieve(table, key, null, null, MAX_INDEX_COLS);
			
			for(KeySlice ks : list) {
				
				for (ColumnOrSuperColumn c : ks.getColumns()) {
					row.put(toString(c.column.name), toString(c.column.value));
				}

				return row;
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return row;
	}

	
	public <K> Map<String, String> get(String table, String column, String[] keys) {
		
		Map<String, String> row = new HashMap<String, String>();
		final List<String> columns = new ArrayList<String>();
		columns.add(column);
		
		try {
			
			for (String key : keys) {

				List<KeySlice> list = retrieve(table, key, columns, null, MAX_INDEX_COLS);

				for (KeySlice ks : list) {

					

					for (ColumnOrSuperColumn c : ks.getColumns()) {
						row.put(key, toString(c.column.value));
					}

					return row;

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return row;
		
	}

	public <K> Map<String, Map<String, String>> get(String table, String[] columnArray, String[] keys) {
		
		final List<String> columns = new ArrayList<String>();
		for(String column : columnArray) {
			columns.add(column);
		}
		
		Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
		
		try {

			Map<ByteBuffer, List<ColumnOrSuperColumn>> input = retrieveMany(table, Arrays.asList(keys), columns);

			Set<ByteBuffer> keyset = input.keySet();

			for (ByteBuffer bb : keyset) {

				List<ColumnOrSuperColumn> cols = input.get(bb);
				String key = toString(bb);

				Map<String, String> row = new HashMap<String, String>(cols.size());

				for (ColumnOrSuperColumn c : cols) {
					row.put(new String(c.column.getName()),
							new String(c.column.getValue()));
				}

				if (row.size() > 0)
					result.put(key, row);
			}

			return result;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private List<KeySlice> retrieve(final String table, final String startKey, final List<String> columns, final Map<String,String> conditions, int count) 
				throws InvalidRequestException, TException, UnsupportedEncodingException, UnavailableException, TimedOutException {
		
		List<KeySlice> results = new ArrayList<KeySlice>();
		
		if(table == null) return results;
		
		TTransport tr = getTransport();
		Cassandra.Client client = getClient();
		
		tr.open();

		client.set_keyspace(Const.KEYSPACE);
		
		ColumnParent parent = new ColumnParent(table);
		
		SlicePredicate predicate = new SlicePredicate();
		
		if(columns == null || columns.size() == 0) {
			SliceRange slice_range = new SliceRange(toByteBuffer(""), toByteBuffer(""), false, count);
			predicate.setSlice_range(slice_range);
		} else {
			predicate.setColumn_names(StringToBB(columns));
		}
		
		KeyRange keyRange = null;
		
		if(conditions == null || conditions.size() == 0) {
			
			keyRange = new KeyRange(1);
			
			if(startKey == null || "".equals(startKey)) {
				throw new TException("Either conditions or key id must be specified");
			}
			
			ByteBuffer sk = toByteBuffer(startKey);
			keyRange.setStart_key(sk);
			keyRange.setEnd_key(sk);
			results = client.get_range_slices(parent, predicate, keyRange, ConsistencyLevel.ONE);
			
		} else {
			
			keyRange = new KeyRange();
			keyRange.setStart_key(new byte[0]);
			keyRange.setEnd_key(new byte[0]);
			
			List<IndexExpression> lista = new ArrayList<IndexExpression>();
			
			Set<String> keyset = conditions.keySet();
			
			for (String k : keyset) {
				
				String value = (String)conditions.get(k);
				IndexExpression ie = new IndexExpression(toByteBuffer(k), IndexOperator.EQ, toByteBuffer(value));
				lista.add(ie);
			}
			String sKey = "";
			if(startKey != null) sKey = startKey;
			IndexClause ic = new IndexClause(lista, toByteBuffer(sKey), count);
			results = client.get_indexed_slices(parent, ic, predicate, ConsistencyLevel.ONE);
		}
		
		tr.close();
		
		return results; 
	}

	private List<ByteBuffer> StringToBB(List<String> columns) throws UnsupportedEncodingException {
		List<ByteBuffer> cols = new ArrayList<ByteBuffer>();
		if(columns != null) {
			for(String s : columns) {
				if(s != null && !"".equals(s)) {
					cols.add(toByteBuffer(s));
				}
			}
		}
		return cols;
	}
	
	public Map<ByteBuffer, List<ColumnOrSuperColumn>> retrieveMany(final String table, final List<String> keys, final List<String> columns) 
			throws InvalidRequestException, TException, UnsupportedEncodingException, UnavailableException, TimedOutException {

		Map<ByteBuffer, List<ColumnOrSuperColumn>> results = new HashMap<ByteBuffer, List<ColumnOrSuperColumn>>();
		
		if(table == null) return results;
		
		TTransport tr = getTransport();
		Cassandra.Client client = getClient();
		tr.open();

		client.set_keyspace(Const.KEYSPACE);
		
		ColumnParent parent = new ColumnParent(table);
		
		SlicePredicate predicate = new SlicePredicate();
		predicate.setColumn_names(StringToBB(columns));
		
		results = client.multiget_slice(StringToBB(keys), parent, predicate, ConsistencyLevel.ONE);
		
		tr.close();
		
		return results; 
	}
	
/*	public List<String> getKeys(final String table) throws UnsupportedEncodingException, InvalidRequestException, TException, UnavailableException, TimedOutException {
		return getKeys(table, null, null);
	}

	public List<String> getKeys(final String table, final String afterKey) throws UnsupportedEncodingException, InvalidRequestException, TException, UnavailableException, TimedOutException {
		return getKeys(table, afterKey, null);
	}
	
	public List<String> getKeys(final String table, final Integer count) throws UnsupportedEncodingException, InvalidRequestException, TException, UnavailableException, TimedOutException {
		return getKeys(table, null, count);
	}
	
	public List<String> getKeys(final String table, final String afterKey, final Integer count) 
			throws InvalidRequestException, TException, UnsupportedEncodingException, UnavailableException, TimedOutException {

		List<String> results = new ArrayList<String>();
		
		if(table == null) return results;
		
		TTransport tr = getTransport();
		Cassandra.Client client = getClient();
		
		tr.open();

		client.set_keyspace(Const.KEYSPACE);
		
		ColumnParent parent = new ColumnParent(table);
		SlicePredicate predicate = new SlicePredicate();
		SliceRange slice_range = new SliceRange(toByteBuffer(""), toByteBuffer(""), false, 0);
		predicate.setSlice_range(slice_range);
		
		KeyRange range = null;
		
		if(count != null && count > 0) {
			range = new KeyRange(count);
		} else {
			range = new KeyRange();
		}
		
		range.setStart_key(new byte[0]);
		range.setEnd_key(new byte[0]);

		if(afterKey != null && !"".equals(afterKey)) {
			range.setStart_key(toByteBuffer(afterKey));
		}

		List<KeySlice> kss = client.get_range_slices(parent, predicate, range, ConsistencyLevel.ONE);
		
		for(KeySlice k : kss) {
			String key = toString(k.getKey());
			results.add(key);
		}
		
		tr.close();
		
		return results; 
	}*/
	
/*    static void loadDataFromFile(String filename) throws FileNotFoundException, UnsupportedEncodingException, InvalidRequestException, UnavailableException, TimedOutException, TException {
    	
    	Scanner scanner = new Scanner(new FileInputStream(filename));
		
    	try {
			
			while (scanner.hasNextLine()) {
				
				String str = scanner.nextLine();
				String[] arr = str.split(",");

				Map<String, String> data = new HashMap<String, String>();
				String key = arr[1];
				data.put("CountryCode", arr[0]);
				data.put("IsOfficial", arr[2]);
				data.put("Percentage", arr[3]);

				store("country", key, data);

			}
		}
	    finally{
	      scanner.close();
	    }
    }*/
    
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, InvalidRequestException, UnavailableException, TimedOutException, TException {

//    	String table = "country";

    	List<String> columns = new ArrayList<String>();
    	columns.add("IsOfficial");
    	columns.add("CountryCode");
    	columns.add("Percentage");
    	
/*    	Map<String, Map<String, String>> ks = retrieveAsMap(table, "Moldova", null, null, 1);
		
    	for (String key : ks.keySet()) {
    		System.out.println(key + " : " + ks.get(key));
    	}
    	
		List<String> keys = new ArrayList<String>();
		keys.add("Jora");
    	Map<String, Map<String, String>> mks = retrieveManyAsMap(table, keys, columns);    	
    	System.out.println("size: " + mks.size());
    	for(String k : mks.keySet()) {
    		System.out.println(k + " : " + mks.get(k));
    	}*/
    	
//    	loadDataFromFile("D:\\sql.txt");    
    	
	}

}
