package my.nosql.datastore;

import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;

public class CassandraDaoFactory {

	private String cluster = "Test Cluster";
	private String host = "localhost";
	private String port = "9160";
	private String keyspace = "xqwf";
	
	private Keyspace ksp;
	
	public CassandraDao makeObject(String columnFamily) {
		if(ksp == null) {
			ksp = HFactory.createKeyspace(keyspace, HFactory.getOrCreateCluster(cluster, host + ":" + port));
		}
		return new CassandraDao(columnFamily, ksp);
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getKeyspace() {
		return keyspace;
	}

	public void setKeyspace(String keyspace) {
		this.keyspace = keyspace;
	}
}
