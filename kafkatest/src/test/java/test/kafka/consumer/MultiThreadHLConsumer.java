package test.kafka.consumer;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadHLConsumer {

	private ExecutorService executor;
	private final ConsumerConnector consumer;
	private final String topic;

	public MultiThreadHLConsumer(String zookeeper, String groupId, String topic) {
		Properties properties = new Properties();
		properties.put("zookeeper.connect", zookeeper);
		properties.put("zk.connect", zookeeper);
		properties.put("group.id", groupId);
		properties.put("zookeeper.session.timeout.ms", "500");
		properties.put("zookeeper.sync.time.ms", "250");
		properties.put("auto.commit.interval.ms", "1000");
		properties.put("auto.offset.reset", "smallest");
		consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(properties));
		this.topic = topic;
	}

	public void testConsumer() {
		Map<String, Integer> topicCount = new HashMap<>();
		topicCount.put(topic, 2);

		Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreams = consumer.createMessageStreams(topicCount);
		List<KafkaStream<byte[], byte[]>> streams = consumerStreams.get(topic);

		executor = Executors.newFixedThreadPool(2);

		int threadNumber = 0;
		for (final KafkaStream<byte[], byte[]> stream : streams) {
			executor.submit(new ConsumerThread(stream, threadNumber));
			threadNumber++;
		}
	}

	public void close() {
		if (consumer != null) {
			consumer.shutdown();
		}
		if (executor != null) {
			executor.shutdown();
		}
	}
}
