package rstest;

import java.util.Properties;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import kafka.utils.Time;

public class KafkaLocalBroker {

	public KafkaServer kafkaServer;

	public KafkaLocalBroker() {
		KafkaConfig kafkaConfig = new KafkaConfig(createProperties());
		Time time = new Time() {
			
			@Override
			public void sleep(long arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public long nanoseconds() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public long milliseconds() {
				// TODO Auto-generated method stub
				return 0;
			}
		};
		kafkaServer = new KafkaServer(kafkaConfig, time );
		kafkaServer.startup();
		System.out.println("embedded kafka is up");

	}

	private Properties createProperties() {
		Properties properties = new Properties(System.getProperties());
		properties.put("zookeeper.connect", "localhost:2181");
		properties.put("broker.id", "0");
		properties.put("enable.zookeeper", "true");
		properties.put("group.id", "testgroup");		
		return properties;
	}

	public void stop() {
		kafkaServer.shutdown();
		System.out.println("embedded kafka stop");
	}
}
