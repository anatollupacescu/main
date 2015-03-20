package test.kafka;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;

public class MultiBrokerProducer {
    private final Producer<Integer, String> producer;
    private final Properties properties = new Properties();

    public MultiBrokerProducer() {
        properties.put("metadata.broker.list", "localhost:9092");
        properties.put("serializer.class", "kafka.serializer.StringEncoder");
//        properties.put("partitioner.class", "test.kafka.SimplePartitioner");
        properties.put("request.required.acks", "1");
        properties.put("advertised.host.name", "localhost");
        ProducerConfig config = new ProducerConfig(properties);
        producer = new Producer<>(config);
    }

    public Producer<Integer, String> getProducer() {
		return producer;
	}
}