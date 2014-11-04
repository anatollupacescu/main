package com.service.datastore.thrift;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class DatastoreBase {

	protected final static int MAX_INDEX_COLS = 100;
	
	private static class holder {
		
		public static final TTransport tr = getTransport();
		public static final Cassandra.Client client = get();
		
		private static TTransport getTransport() {
		    return new TFramedTransport(new TSocket("192.168.16.115", 9160));
		}
		
		private static Cassandra.Client get() {
		    TProtocol proto = new TBinaryProtocol(tr);
		    return new Cassandra.Client(proto);
		}
	}
	
	Cassandra.Client getClient() {
		return holder.client;
	}
	
	TTransport getTransport() {
		return holder.tr;
	}
	
    ByteBuffer toByteBuffer(String value) throws UnsupportedEncodingException
    {
        return ByteBuffer.wrap(value.getBytes("UTF-8"));
    }
    
    String toString(ByteBuffer buffer) throws UnsupportedEncodingException
    {
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return new String(bytes, "UTF-8");
    }
    
    String toString(byte[] bytes) throws UnsupportedEncodingException {
    	return new String(bytes, "UTF-8");
    }
}
