package demo.config;

import java.util.Properties;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.test.TestingServer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import demo.bean.SingleThreadProducer;
import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;

@Configuration
@Profile("producer")
public class ProducerConfiguration {

//	@Bean
	public TestingServer testServer(@Value("${zookeeper.port}") int zookeeperPort) throws Exception {
		return new TestingServer(zookeeperPort);
	}

//	@Bean
	public CuratorFramework framework(TestingServer testServer) {
		return CuratorFrameworkFactory.newClient(testServer.getConnectString(), new RetryOneTime(2000));
	}

//	@Bean
	public KafkaServer kafkaServer(
			@Value("${kafka.zookeeper.connect}") String zookeeperConnect,
			@Value("${kafka.broker.id}") String brokerId,
			CuratorFramework framework)
	{
			Properties properties = new Properties();
			properties.put("zookeeper.connect", zookeeperConnect);
			properties.put("broker.id", brokerId);
			KafkaConfig kafkaConfig = new KafkaConfig(properties);
			return new KafkaServer(kafkaConfig, new kafka.utils.Time() {
				public void sleep(long ms) {}
				public long nanoseconds() {	return 0;}
				public long milliseconds() { return 0; }
			});
	}
	
	@Bean
	public KafkaProducer<byte[], byte[]> producer(
			@Value("${producer.metadata.broker.list}") String metadataBrokerList,
			@Value("${producer.serializer.class}") String serializerClass,
			@Value("${producer.request.required.acks}") String requestRequiredAcks) {	//, KafkaServer server
		final Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, metadataBrokerList);
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializerClass);
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, serializerClass);
		properties.put(ProducerConfig.RETRIES_CONFIG, requestRequiredAcks);
		return new KafkaProducer<byte[], byte[]>(properties);
	}

	@Bean
	public SingleThreadProducer producerTester(
			KafkaProducer<byte[], byte[]> producer, 
			@Value("${kafka.topic}") String topic) 
	{
		return new SingleThreadProducer(producer, topic);
	}
}
