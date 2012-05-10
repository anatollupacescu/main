package core.datastore;

import static me.prettyprint.hector.api.factory.HFactory.createColumn;
import static me.prettyprint.hector.api.factory.HFactory.createColumnQuery;
import static me.prettyprint.hector.api.factory.HFactory.createKeyspace;
import static me.prettyprint.hector.api.factory.HFactory.createMutator;
import static me.prettyprint.hector.api.factory.HFactory.getOrCreateCluster;

import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

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
import core.Const;

public class Cassandra {

	private final static String HOST_PORT = "localhost:9160";
	private final static String CLUSTER = "MyCluster";
	private final static String KEYSPACE = "xqem";
	private final static String TABLE = "Entity";
	private final static String INDEXED_COLUMN = Const.TYPE;
	
	private final static Keyspace keyspace = createKeyspace(KEYSPACE, getOrCreateCluster(CLUSTER, HOST_PORT));
	
	private static final class holder {
		private final static Cassandra cassandra = new Cassandra();
	}

	public static final Cassandra getInstance() {
		return holder.cassandra;
	}
	
	public Mutator<String> getMutator() {
		return createMutator(keyspace, StringSerializer.get());
	}

	public void addInsertion(Mutator<String> m, String key, String column, String value) {
		m.addInsertion(key, TABLE, createColumn(column, value, keyspace.createClock(), StringSerializer.get(), StringSerializer.get()));
	}
	
	public void insert(final String table, final String column, final String key, final String value) {
		getMutator().insert(key, table, createColumn(column, value, StringSerializer.get(), StringSerializer.get()));
	}
	
	public String get(final String table, final String key, final String column) throws HectorException {
		ColumnQuery<String, String, String> q = createColumnQuery(keyspace, StringSerializer.get(), StringSerializer.get(), StringSerializer.get());
		QueryResult<HColumn<String, String>> r = q.setKey(key).setName(column).setColumnFamily(table).execute();
		HColumn<String, String> c = r.get();
		return c == null ? null : c.getValue();
	}

	public void delete(final String table, String column, String ... keys) {
		Mutator<String> m = getMutator();
		for (String key : keys) {
			m.addDeletion(key, table, column, StringSerializer.get());
		}
		m.execute();
	}

	public IndexedSlicesQuery<String, String, String> getIndexedQuery(final String type) {
		IndexedSlicesQuery<String, String, String> indexedSlicesQuery = HFactory.createIndexedSlicesQuery(keyspace, StringSerializer.get(), StringSerializer.get(), StringSerializer.get());
		indexedSlicesQuery.setColumnFamily(TABLE);
		indexedSlicesQuery.setStartKey("");
		indexedSlicesQuery.addEqualsExpression(INDEXED_COLUMN, type);
		return indexedSlicesQuery;
	}
	
	public MultigetSliceQuery<String, String, String> getMultigetQuery(String type, String[] keys) {
		MultigetSliceQuery<String, String, String> q = HFactory.createMultigetSliceQuery(keyspace, StringSerializer.get(), StringSerializer.get(), StringSerializer.get());
		q.setColumnFamily(TABLE);
		q.setKeys(keys);
		return q;
	}
	
	public String executeQuery(List<Query> queries) throws XMLStreamException {

		XMLOutputFactory xof = XMLOutputFactory.newInstance();

		Writer stream = new StringWriter();
		XMLStreamWriter xtw = xof.createXMLStreamWriter(stream);
		xtw.writeStartElement("response");
		for (Query q : queries) {

			if(q == null) continue;
			
			Rows<String, String, String> rows = null;
			
			if (q.indexedQuery != null) {
				IndexedSlicesQuery<String, String, String> query = q.indexedQuery;
				QueryResult<OrderedRows<String, String, String>> result = query.execute();
				rows = result.get();
			} else {
				MultigetSliceQuery<String, String, String> query = q.multigetQuery;
				QueryResult<Rows<String, String, String>> result = query.execute();
				rows = result.get();
			}
			
			for (Row<String, String, String> r : rows) {

				ColumnSlice<String, String> colSlice = r.getColumnSlice();
				
				if(colSlice.getColumns().size() == 0) continue;
				
				xtw.writeStartElement(q.type);
				xtw.writeAttribute("key", r.getKey());

				for (HColumn<String, String> column : colSlice.getColumns()) {
					xtw.writeStartElement(column.getName());
					xtw.writeCharacters(column.getValue());
					xtw.writeEndElement();
				}

				xtw.writeEndElement();
			}
		}
		xtw.writeEndElement();
		xtw.flush();
		xtw.close();
		
		return stream.toString();
	}
}
