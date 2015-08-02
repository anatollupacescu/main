package org.kafka.tool.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import org.kafka.tool.bean.SingleThreadConsumer;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.javaapi.consumer.ConsumerConnector;

@Configuration
@Profile("consumer")
public class ConsumerConfiguration {

	@Bean
	public SingleThreadConsumer consumerTester(ConsumerConnector consumer) {
		return new SingleThreadConsumer();
	}

	@Bean
	public ConsumerConnector consumer(
			@Value("${consumer.zookeeper.connect}") String zookeeper,
			@Value("${consumer.group.id}") String groupId,
			@Value("${consumer.client.id}") String clientid)
	{
		Properties properties = new Properties();
		properties.put("zookeeper.connect", zookeeper);
		properties.put("group.id", groupId);
        properties.put("client.id", clientid);
		ConsumerConfig config = new ConsumerConfig(properties);
		return Consumer.createJavaConsumerConnector(config);
	}
}
