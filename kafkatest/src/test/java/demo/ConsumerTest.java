package demo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.junit.Test;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import kafka.serializer.Decoder;
import kafka.serializer.StringDecoder;

public class ConsumerTest {

	private final Logger log = Logger.getAnonymousLogger();

	final String topic = "task-output";

	@Test
	public void consumerTest1() {

		Properties properties = new Properties();
		properties.put("zookeeper.connect", "7ce9e795682a:2181");
		properties.put("group.id", "test");
		properties.put("auto.offset.reset", "smallest");
		ConsumerConfig config = new ConsumerConfig(properties);
		ConsumerConnector consumer = Consumer.createJavaConsumerConnector(config);

		Map<String, Integer> topicCount = new HashMap<>();
		topicCount.put(topic, 1);
		Decoder<String> decoder = new StringDecoder(null);
		Map<String, List<KafkaStream<String, String>>> consumerStreamsMap = consumer.createMessageStreams(topicCount, decoder, decoder);
		List<KafkaStream<String, String>> consumerStreams = consumerStreamsMap.values().iterator().next();
		Iterator<KafkaStream<String, String>> it = consumerStreams.iterator();
		KafkaStream<String, String> stream1 = it.next();
		ConsumerIterator<String, String> streamIt = stream1.iterator();
		while (streamIt.hasNext()) {
			MessageAndMetadata<String, String> message = streamIt.next();
			log.info(new String(message.message()));
		}
		System.out.println("done");
		consumer.shutdown();
	}
}
