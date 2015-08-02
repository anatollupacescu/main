package org.kafka.tool.config;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import org.kafka.tool.bean.SingleThreadProducer;

@Configuration
@Profile("producer")
public class ProducerConfiguration {

	@Bean
	public Properties producerConfigurationProperties(
			@Value("${producer.metadata.broker.list}") String metadataBrokerList,
			@Value("${producer.serializer.class}") String serializerClass,
			@Value("${producer.request.required.acks}") String requestRequiredAcks) {
		final Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, metadataBrokerList);
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializerClass);
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, serializerClass);
		properties.put(ProducerConfig.RETRIES_CONFIG, requestRequiredAcks);
		properties.put("metadata.broker.list", metadataBrokerList);
		properties.put("serializer.class", "kafka.serializer.StringEncoder");
		return properties;
	}

	@Bean
	public Producer<Integer, String> producer(Properties producerConfigurationProperties) {
		return new KafkaProducer<Integer, String>(producerConfigurationProperties);
	}

	@Bean
	public kafka.javaapi.producer.Producer<Integer, String> kafkaProducer(Properties producerConfigurationProperties) {
		kafka.producer.ProducerConfig producerConfig = new kafka.producer.ProducerConfig(
				producerConfigurationProperties);
		return new kafka.javaapi.producer.Producer<>(producerConfig);
	}

	@Bean
	public SingleThreadProducer producerTester(Producer<Integer, String> producer) {
		return new SingleThreadProducer();
	}
}
