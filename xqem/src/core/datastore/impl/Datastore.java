package core.datastore.impl;

import java.util.Map;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;

import me.prettyprint.cassandra.model.IndexedSlicesQuery;
import me.prettyprint.hector.api.mutation.Mutator;
import core.datastore.IDatastore;
import core.datastore.pull.Query;

public class Datastore extends DatastoreBase implements IDatastore {

	private final String TABLE="Entity";
	
	private final static Logger logger = Logger.getLogger(Datastore.class.getName());
	
	private static final class holder {
		private static final Datastore datastore = new Datastore();
	}
	
	public static final Datastore getInstance() {
		return holder.datastore;
	}
	
	public void store(String table, String column, String key, String value) {
		insert(table, column, key, value);
	}

	public void store(String table, Map<String, Map<String, String>> keyColValues) {
		insertMulti(table, keyColValues);
	}
	
	public void store(String table, String key, Map<String, String> colValues) {
		insertMulti(table, key, colValues);
	}
	
	public void delete(String table, String column, String ... keys) {
		delete(table, column, keys);
	}
	
	/*get*/
	public String get(String table, String column, String key) {
		return get(table, key, column);
	}
	
	public Map<String, String> get(String table, String key) {
		return getMulti(table, key);
	}
	
	public Map<String, String> get(String table, String column, String[] keys) {
		return getMulti(table, column, keys);
	}
	
	public Map<String, Map<String, String>> get(String table, String[] column, String[] keys) {
		return getMulti(table, column, keys);
	}
	
	public IndexedSlicesQuery<String, String, String> getQuery(String type) {
		return super.getQuery(type);
	}
	
	public Map<String, Map<String, String>> query(Query q) {
		return super.query(q);
	}
	
	public String queryXML(Query...q) throws XMLStreamException {
		return super.queryNativeXML(q);
	}
	
	public Mutator<String> getMutator() {
		return super.getMutator();
	}
	
	public void addInsertion(Mutator<String> m, String key, String column, String value) {
		logger.info("key=" + key + " column=" + column + " value=" + value);
		super.addInsertion(m, TABLE, key, column, value);
	}
}
