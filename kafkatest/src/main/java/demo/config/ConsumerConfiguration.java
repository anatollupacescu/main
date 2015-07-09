package demo.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import demo.bean.MultiThreadConsumer;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.server.KafkaServer;

@Configuration
@Profile("consumer")
public class ConsumerConfiguration {

	@Bean
	public MultiThreadConsumer consumerTester(ConsumerConnector consumer, 
			@Value("topic") String topic,
			@Value("${consumer.message.count}") int messageCount) 
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
}
