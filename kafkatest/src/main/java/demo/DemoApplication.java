package demo;

import java.util.Properties;
import java.util.logging.Logger;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.test.TestingServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import demo.bean.MultiThreadConsumer;
import demo.bean.SingleThreadProducer;

@SpringBootApplication
public class DemoApplication {

	private static final Logger log = Logger.getAnonymousLogger();

	private static final int messageCount = 10;

	public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext context = SpringApplication.run(DemoApplication.class, args);
        MultiThreadConsumer consumerTester = context.getBean(MultiThreadConsumer.class);
        KafkaServer kafkaServer = context.getBean(KafkaServer.class);
        SingleThreadProducer producer = context.getBean(SingleThreadProducer.class);
        CuratorFramework framework = context.getBean(CuratorFramework.class);

        log.info("Kafka starting...");
        kafkaServer.startup();
        
        log.info("Starting consumer...");
        consumerTester.testConsumer();
        
        log.info("Publishing messages...");
		producer.sendMessages(10);
		
		log.info("Shutting down...");
		consumerTester.close();
		kafkaServer.shutdown();
		framework.close();
    }

	@Bean
	public TestingServer testServer(@Value("${zookeeper.port}") int zookeeperPort) throws Exception {
		return new TestingServer(zookeeperPort);
	}

	@Bean
	public CuratorFramework framework(TestingServer testServer) {
		return CuratorFrameworkFactory.newClient(testServer.getConnectString(), new RetryOneTime(2000));
	}

	@Bean
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
	public SingleThreadProducer producerTester(Producer<Integer, String> producer, 
			@Value("topic") String topic) 
	{
		return new SingleThreadProducer(producer, topic);
	}
	
	@Bean
	public MultiThreadConsumer consumerTester(ConsumerConnector consumer, 
			@Value("topic") String topic) 
	{
		return new MultiThreadConsumer(consumer, topic, messageCount);
	}
	
	@Bean
	public ConsumerConnector consumer(
			@Value("${consumer.zookeeper.connect}") String zookeeper,
			@Value("${consumer.group.id}") String groupId,
			@Value("${consumer.zookeeper.session.timeout.ms}") String timeout,
			@Value("${consumer.zookeeper.sync.time.ms}") String sync,
			@Value("${consumer.auto.commit.interval.ms}") String interval,
			@Value("${consumer.auto.offset.reset}") String offset,
			KafkaServer kafkaServer) 
	{
		Properties properties = new Properties();
		properties.put("zookeeper.connect", zookeeper);
		properties.put("zk.connect", zookeeper);
		properties.put("group.id", groupId);
		properties.put("zookeeper.session.timeout.ms", timeout);
		properties.put("zookeeper.sync.time.ms", sync);
		properties.put("auto.commit.interval.ms", interval);
		properties.put("auto.offset.reset", offset);
		return Consumer.createJavaConsumerConnector(new ConsumerConfig(properties));
	}

	@Bean
	public Producer<Integer, String> producer(
			@Value("${producer.metadata.broker.list}") String metadataBrokerList,
			@Value("${producer.serializer.class}") String serializerClass,
			@Value("${producer.request.required.acks}") String requestRequiredAcks,
			KafkaServer kafkaServer) {
		/*
		 * properties.put("partitioner.class","test.kafka.SimplePartitioner");
		 */
		final Properties properties = new Properties();
		properties.put("metadata.broker.list", metadataBrokerList);
		properties.put("serializer.class", serializerClass);
		properties.put("request.required.acks", requestRequiredAcks);
		return new Producer<>(new ProducerConfig(properties));
	}
}
