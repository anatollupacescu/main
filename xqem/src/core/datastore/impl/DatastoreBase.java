package core.datastore.impl;

import static me.prettyprint.hector.api.factory.HFactory.createColumn;
import static me.prettyprint.hector.api.factory.HFactory.createColumnQuery;
import static me.prettyprint.hector.api.factory.HFactory.createKeyspace;
import static me.prettyprint.hector.api.factory.HFactory.createMultigetSliceQuery;
import static me.prettyprint.hector.api.factory.HFactory.createMutator;
import static me.prettyprint.hector.api.factory.HFactory.getOrCreateCluster;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import me.prettyprint.cassandra.model.IndexedSlicesQuery;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.beans.Rows;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.ColumnQuery;
import me.prettyprint.hector.api.query.MultigetSliceQuery;
import me.prettyprint.hector.api.query.QueryResult;
import core.datastore.query.Query;

public class DatastoreBase {

	private final static String KEYSPACE = "mywoe";
	private final static String HOST_PORT = "localhost:9160";
	private final static int MAX_INDEX_COLS = 100;
	
	private static final class holder {
		private final static StringSerializer serializer = StringSerializer.get();
		private final static Keyspace keyspace = createKeyspace(KEYSPACE,getOrCreateCluster("MyCluster", HOST_PORT));
	}

	protected Mutator<String> getMutator() {
		return createMutator(holder.keyspace, StringSerializer.get() );
	}
	
	protected void insert(final String CF_NAME, final String COLUMN_NAME, final String key, final String value) {
		getMutator().insert(key, CF_NAME,createColumn(COLUMN_NAME, value, holder.serializer, holder.serializer));
	}
	
	protected String get(final String CF_NAME, final String key, final String COLUMN_NAME) throws HectorException {
		ColumnQuery<String, String, String> q = createColumnQuery(holder.keyspace, StringSerializer.get(), holder.serializer, holder.serializer);
		QueryResult<HColumn<String, String>> r = q.setKey(key).setName(COLUMN_NAME).setColumnFamily(CF_NAME).execute();
		HColumn<String, String> c = r.get();
		return c == null ? null : c.getValue();
	}

	protected Map<String, String> getMulti(final String CF_NAME, String key) {
		
		MultigetSliceQuery<String, String, String> q = createMultigetSliceQuery(holder.keyspace, StringSerializer.get(), holder.serializer,holder.serializer);
		
		q.setColumnFamily(CF_NAME);
		q.setKeys(key);
		q.setRange("", "", false, MAX_INDEX_COLS);

		QueryResult<Rows<String, String, String>> r = q.execute();
		Rows<String, String, String> rows = r.get();
		
		List<HColumn<String, String>> columns = rows.getByKey(key).getColumnSlice().getColumns();
		Map<String, String> ret = new HashMap<String, String>(columns.size());	
		
		for (HColumn<String, String> c : columns) {
			if (c != null && c.getValue() != null) {
				ret.put(c.getName(), c.getValue());
			}
		}
		return ret;
	}
	
	protected Map<String, String> getMulti(final String CF_NAME, String COLUMN_NAME, String... keys) {
		
		MultigetSliceQuery<String, String, String> q = createMultigetSliceQuery(holder.keyspace, StringSerializer.get(), holder.serializer,holder.serializer);
		
		q.setColumnFamily(CF_NAME);
		q.setKeys(keys);
		q.setColumnNames(COLUMN_NAME);

		QueryResult<Rows<String, String, String>> r = q.execute();
		Rows<String, String, String> rows = r.get();
		Map<String, String> ret = new HashMap<String, String>(keys.length);
		for (String k : keys) {
			HColumn<String, String> c = rows.getByKey(k).getColumnSlice().getColumnByName(COLUMN_NAME);
			if (c != null && c.getValue() != null) {
				ret.put(k, c.getValue());
			}
		}
		return ret;
	}

	protected Map<String, Map<String, String>> getMulti(final String CF_NAME, String[] COLUMN_NAME, String... keys) {
		
		MultigetSliceQuery<String, String, String> q = createMultigetSliceQuery(holder.keyspace, StringSerializer.get(), holder.serializer,holder.serializer);
		
		q.setColumnFamily(CF_NAME);
		q.setKeys(keys);
		q.setColumnNames(COLUMN_NAME);

		QueryResult<Rows<String, String, String>> r = q.execute();
		Rows<String, String, String> rows = r.get();
		
		Map<String, Map<String, String>> ret = new HashMap<String, Map<String, String>>(keys.length);
		
		for (String k : keys) {
			
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
	
	protected void insertMulti(final String CF_NAME, Map<String, Map<String, String>> keyValues) {
		
		Mutator<String> m = getMutator();
		
		for(String k : keyValues.keySet()) {
			
			Map<String, String> map = keyValues.get(k);
			
			for (Map.Entry<String, String> keyValue : map.entrySet()) {
				
				m.addInsertion(k, CF_NAME, createColumn(keyValue.getKey(), keyValue.getValue(), holder.keyspace.createClock(), holder.serializer, holder.serializer));
			
			}
			
		}		
		m.execute();
	}

	protected void insertMulti(final String CF_NAME, String key, Map<String, String> colValues) {
		
		Mutator<String> m = getMutator();//createMutator(holder.keyspace, StringSerializer.get() );
		
		for (Map.Entry<String, String> keyValue : colValues.entrySet()) {
			m.addInsertion(key, CF_NAME, createColumn(keyValue.getKey(), keyValue.getValue(), holder.keyspace.createClock(), holder.serializer, holder.serializer));
		}
		
		m.execute();
	}
	
	protected void delete(final String CF_NAME, String COLUMN_NAME, String ... keys) {

		Mutator<String> m = getMutator();
		
		for (String key : keys) {
			m.addDeletion(key, CF_NAME, COLUMN_NAME, holder.serializer);
		}
		m.execute();
	}

	protected IndexedSlicesQuery<String, String, String> getQuery(final String type) {
		StringSerializer ss = StringSerializer.get();
		IndexedSlicesQuery<String, String, String> indexedSlicesQuery = HFactory.createIndexedSlicesQuery(holder.keyspace, ss, ss, ss);
		indexedSlicesQuery.setColumnFamily("Entity");
		indexedSlicesQuery.setStartKey("");
		indexedSlicesQuery.addEqualsExpression("type", type);

		return indexedSlicesQuery;
	}
	
	protected Map<String, Map<String, String>> query(Query q) {

		IndexedSlicesQuery<String, String, String> query = q.getQuery();
		
		QueryResult<OrderedRows<String, String, String>> result = query.execute();
		
		OrderedRows<String, String, String> rows = result.get();
		
		Map<String, Map<String, String>> ret = new HashMap<String, Map<String, String>>(rows.getCount());
		
		for(Row<String, String, String> r : rows) {
			
			ColumnSlice<String, String> colSlice = r.getColumnSlice();
			
			Map<String, String> map = new HashMap<String, String>();
			
			for(HColumn<String, String> column : colSlice.getColumns() ) {
				String columnName = column.getName();
				String columnValue = column.getValue();
				map.put(columnName, columnValue);
			}
			
			ret.put(r.getKey(), map);
		}
		
		return ret;
	}
	
	protected String queryNativeXML(Query... qs) throws XMLStreamException {

		XMLOutputFactory xof = XMLOutputFactory.newInstance();

		Writer stream = new StringWriter();
		XMLStreamWriter xtw = xof.createXMLStreamWriter(stream);
		xtw.writeStartDocument("utf-8", "1.0");
		xtw.writeStartElement("response");

		for (Query q : qs) {

			IndexedSlicesQuery<String, String, String> query = q.getQuery();
			
			QueryResult<OrderedRows<String, String, String>> result = query.execute();
			
			OrderedRows<String, String, String> rows = result.get();
			
			for(Row<String, String, String> r : rows) {
				
				xtw.writeStartElement(q.getType());
				xtw.writeAttribute("key", r.getKey());
				
				ColumnSlice<String, String> colSlice = r.getColumnSlice();
				
				for(HColumn<String, String> column : colSlice.getColumns()) {
					xtw.writeStartElement(column.getName());
					xtw.writeCharacters(column.getValue());
					xtw.writeEndElement();
				}
				
				xtw.writeEndElement();
			}
			xtw.writeEndElement();
			xtw.writeEndDocument();
			xtw.flush();
			xtw.close();
		}
		
		return stream.toString();
	}

	protected void addInsertion(Mutator<String> m, String table, String key, String column, String value) {
		m.addInsertion(key, table, createColumn(column, value, holder.keyspace.createClock(), holder.serializer, holder.serializer));
	}
}
