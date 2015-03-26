package demo.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class MultiThreadConsumer {

	private ExecutorService executor;
	private final ConsumerConnector consumer;
	private final String topic;

	public MultiThreadConsumer(ConsumerConnector consumer, String topic) {
		this.consumer = consumer;
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

	private final static class ConsumerThread implements Runnable {

		private KafkaStream<byte[], byte[]> stream;
		private int threadNumber;

		public ConsumerThread(KafkaStream<byte[], byte[]> stream, int threadNumber) {
			this.threadNumber = threadNumber;
			this.stream = stream;
		}

		public void run() {
			ConsumerIterator<byte[], byte[]> it = stream.iterator();
			while (it.hasNext()) {
				System.out.println("Message from thread " + threadNumber + ": " + new String(it.next().message()));
			}
			System.out.println("Shutting down thread: " + threadNumber);
		}
	}
}
