package org.kafka.tool.config;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import org.kafka.tool.bean.SimplePartitioner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import org.kafka.tool.bean.SingleThreadProducer;

@Configuration
@Profile("producer")
public class ProducerConfiguration {

    @Bean
    public SingleThreadProducer producerTester(Producer<byte[], String> producer) {
        return new SingleThreadProducer();
    }

	@Bean
	public Producer<byte[], String> kafkaProducer(
			@Value("${producer.metadata.broker.list}") String metadataBrokerList,
			@Value("${producer.serializer.class}") String serializerClass,
            @Value("${producer.key.serializer.class}") String keySerializerClass,
			@Value("${producer.request.required.acks}") String requestRequiredAcks)
    {
		final Properties properties = new Properties();
		properties.put("metadata.broker.list", metadataBrokerList);
		properties.put("serializer.class", serializerClass);
        properties.put("key.serializer.class", keySerializerClass);
		properties.put("request.required.acks", requestRequiredAcks);
        properties.put("partitioner.class", SimplePartitioner.class.getCanonicalName());
		ProducerConfig producerConfig = new ProducerConfig(properties);
		return new Producer<>(producerConfig);
	}
}
