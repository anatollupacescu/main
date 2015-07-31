package demo.config;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import demo.bean.SingleThreadProducer;

@Configuration
@Profile("producer")
public class ProducerConfiguration {

	@Bean
	public Producer<Integer, String> producer(
			@Value("${producer.metadata.broker.list}") String metadataBrokerList,
			@Value("${producer.serializer.class}") String serializerClass,
			@Value("${producer.request.required.acks}") String requestRequiredAcks) {	//, KafkaServer server
		final Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, metadataBrokerList);
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializerClass);
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, serializerClass);
		properties.put(ProducerConfig.RETRIES_CONFIG, requestRequiredAcks);
		return new KafkaProducer<Integer, String>(properties);
	}

	@Bean
	public SingleThreadProducer producerTester(
			Producer<Integer, String> producer, 
			@Value("${kafka.topic}") String topic) 
	{
		return new SingleThreadProducer(producer, topic);
	}
}
