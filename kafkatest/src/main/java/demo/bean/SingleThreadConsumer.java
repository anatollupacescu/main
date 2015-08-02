package demo.bean;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class SingleThreadConsumer {

	private @Autowired Logger logger;

	private @Autowired ConsumerConnector consumer;

	public void readMessages(String topic, Integer messageCount) {
		Preconditions.checkNotNull(topic);
		Preconditions.checkNotNull(messageCount);
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreams = consumer.createMessageStreams(ImmutableMap.of(topic, 1));
		ConsumerIterator<byte[], byte[]> stream = consumerStreams.values().iterator().next().iterator().next().iterator();
		int counter = 0;
		while (stream.hasNext() && counter < Integer.valueOf(messageCount)) {
			logger.info("Message received: {}", new String(stream.next().message()));
			counter++;
		}
	}

	public void shutdown() {
		consumer.shutdown();
	}
}
