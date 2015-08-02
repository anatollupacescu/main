package org.kafka.tool.bean;

import java.util.List;
import java.util.Map;

import kafka.message.MessageAndMetadata;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.springframework.beans.factory.annotation.Value;

public class SingleThreadConsumer {

	private @Value("${kafka.topic}") String topic;

	private @Value("${message.count}") String messageCount;

	private @Autowired ConsumerConnector consumer;

	public void readMessages(final Logger logger) {
		Preconditions.checkNotNull(topic);
		Preconditions.checkNotNull(messageCount);
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreams = consumer.createMessageStreams(ImmutableMap.of(topic, 1));
		ConsumerIterator<byte[], byte[]> stream = consumerStreams.values().iterator().next().iterator().next().iterator();
		int counter = 0;
		while (counter++ < Integer.valueOf(messageCount) && stream.hasNext()) {
			MessageAndMetadata<byte[], byte[]> messageMetadata = stream.next();
			if(messageMetadata != null && messageMetadata.message() != null) {
				logger.info("Offset: {}, Message: {}", messageMetadata.offset(), new String(messageMetadata.message()));
			}
		}
	}

	public void shutdown() {
		consumer.shutdown();
	}
}
