package com.service.datastore.hector;

import static me.prettyprint.hector.api.factory.HFactory.createColumn;
import static me.prettyprint.hector.api.factory.HFactory.createColumnQuery;
import static me.prettyprint.hector.api.factory.HFactory.createKeyspace;
import static me.prettyprint.hector.api.factory.HFactory.createMultigetSliceQuery;
import static me.prettyprint.hector.api.factory.HFactory.createMutator;
import static me.prettyprint.hector.api.factory.HFactory.getOrCreateCluster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.Rows;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.ColumnQuery;
import me.prettyprint.hector.api.query.MultigetSliceQuery;
import me.prettyprint.hector.api.query.QueryResult;

public class DatastoreBase {

	private final static String KEYSPACE = "discussion";
	private final static String HOST_PORT = "192.168.16.115:9160";
	private final static int MAX_INDEX_COLS = 100;
	
	private static final class holder {
		private final static StringSerializer serializer = StringSerializer.get();
		private final static Keyspace keyspace = createKeyspace(KEYSPACE,getOrCreateCluster("MyCluster", HOST_PORT));
	}

	<K> void insert(final String CF_NAME, final String COLUMN_NAME, final K key, final String value, Serializer<K> keySerializer) {
		createMutator(holder.keyspace, keySerializer).insert(key, CF_NAME,createColumn(COLUMN_NAME, value, holder.serializer, holder.serializer));
	}
	
	<K> String get(final String CF_NAME, final K key, Serializer<K> keySerializer, final String COLUMN_NAME) throws HectorException {
		ColumnQuery<K, String, String> q = createColumnQuery(holder.keyspace, keySerializer, holder.serializer, holder.serializer);
		QueryResult<HColumn<String, String>> r = q.setKey(key).setName(COLUMN_NAME).setColumnFamily(CF_NAME).execute();
		HColumn<String, String> c = r.get();
		return c == null ? null : c.getValue();
	}

	@SuppressWarnings("unchecked")
	<K> Map<String, String> getMulti(final String CF_NAME, Serializer<K> keySerializer, K key) {
		
		MultigetSliceQuery<K, String, String> q = createMultigetSliceQuery(holder.keyspace, keySerializer, holder.serializer,holder.serializer);
		
		q.setColumnFamily(CF_NAME);
		q.setKeys(key);
		q.setRange("", "", false, MAX_INDEX_COLS);

		QueryResult<Rows<K, String, String>> r = q.execute();
		Rows<K, String, String> rows = r.get();
		
		List<HColumn<String, String>> columns = rows.getByKey(key).getColumnSlice().getColumns();
		Map<String, String> ret = new HashMap<String, String>(columns.size());	
		
		for (HColumn<String, String> c : columns) {
			if (c != null && c.getValue() != null) {
				ret.put(c.getName(), c.getValue());
			}
		}
		return ret;
	}
	
	<K> Map<K, String> getMulti(final String CF_NAME, Serializer<K> keySerializer, String COLUMN_NAME, K... keys) {
		
		MultigetSliceQuery<K, String, String> q = createMultigetSliceQuery(holder.keyspace, keySerializer, holder.serializer,holder.serializer);
		
		q.setColumnFamily(CF_NAME);
		q.setKeys(keys);
		q.setColumnNames(COLUMN_NAME);

		QueryResult<Rows<K, String, String>> r = q.execute();
		Rows<K, String, String> rows = r.get();
		Map<K, String> ret = new HashMap<K, String>(keys.length);
		for (K k : keys) {
			HColumn<String, String> c = rows.getByKey(k).getColumnSlice().getColumnByName(COLUMN_NAME);
			if (c != null && c.getValue() != null) {
				ret.put(k, c.getValue());
			}
		}
		return ret;
	}

	<K> Map<K, Map<String, String>> getMulti(final String CF_NAME, Serializer<K> keySerializer, String[] COLUMN_NAME, K... keys) {
		
		MultigetSliceQuery<K, String, String> q = createMultigetSliceQuery(holder.keyspace, keySerializer, holder.serializer,holder.serializer);
		
		q.setColumnFamily(CF_NAME);
		q.setKeys(keys);
		q.setColumnNames(COLUMN_NAME);

		QueryResult<Rows<K, String, String>> r = q.execute();
		Rows<K, String, String> rows = r.get();
		
		Map<K, Map<String, String>> ret = new HashMap<K, Map<String, String>>(keys.length);
		
		for (K k : keys) {
			
			List<HColumn<String, String>> columns = rows.getByKey(k).getColumnSlice().getColumns();
			
			Map<String, String> row = new HashMap<String, String>(columns.size());
			
			for (HColumn<String, String> c : columns) {

				if (c != null && c.getValue() != null) {
					row.put(c.getName(), c.getValue());
				}
				
			}
			
			ret.put(k, row);
			
		}
		
		return ret;
	}

	
	<K> void insertMulti(final String CF_NAME, Map<K, Map<String, String>> keyValues, Serializer<K> keySerializer) {
		
		Mutator<K> m = createMutator(holder.keyspace, keySerializer);
		
		for(K k : keyValues.keySet()) {
			
			Map<String, String> map = keyValues.get(k);
			
			for (Map.Entry<String, String> keyValue : map.entrySet()) {
				
				m.addInsertion(k, CF_NAME, createColumn(keyValue.getKey(), keyValue.getValue(), holder.keyspace.createClock(), holder.serializer, holder.serializer));
			
			}
			
		}		
		m.execute();
	}

	<K> void insertMulti(final String CF_NAME, K key, Map<String, String> colValues, Serializer<K> keySerializer) {
		
		Mutator<K> m = createMutator(holder.keyspace, keySerializer);
		
		for (Map.Entry<String, String> keyValue : colValues.entrySet()) {
			m.addInsertion(key, CF_NAME, createColumn(keyValue.getKey(), keyValue.getValue(), holder.keyspace.createClock(), holder.serializer, holder.serializer));
		}
		
		m.execute();
	}
	
	<K> void delete(final String CF_NAME, Serializer<K> keySerializer, String COLUMN_NAME, K... keys) {
		Mutator<K> m = createMutator(holder.keyspace, keySerializer);
		for (K key : keys) {
			m.addDeletion(key, CF_NAME, COLUMN_NAME, holder.serializer);
		}
		m.execute();
	}
}
