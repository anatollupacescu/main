package demo.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class MultiThreadConsumer {

	private ExecutorService executor;
	private final ConsumerConnector consumer;
	private final String topic;
	private final int count;
	private int threadCount = 2;
	private CountDownLatch latch = new CountDownLatch(threadCount);
	
	public MultiThreadConsumer(ConsumerConnector consumer, String topic, int messageCount) {
		this.consumer = consumer;
		this.topic = topic;
		this.count = messageCount;
	}

	public void testConsumer() {
		Map<String, Integer> topicCount = new HashMap<>();
		topicCount.put(topic, 2);

		Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreams = consumer.createMessageStreams(topicCount);
		List<KafkaStream<byte[], byte[]>> streams = consumerStreams.get(topic);

		executor = Executors.newFixedThreadPool(threadCount);

		int messagesToBeConsumedByOneThread = count / threadCount;

		int threadNumber = 0;
		for (final KafkaStream<byte[], byte[]> stream : streams) {
			executor.submit(new ConsumerThread(stream, threadNumber, messagesToBeConsumedByOneThread, latch));
			threadNumber++;
		}
	}

	public void close() throws InterruptedException {

		latch.await();

		if (executor != null) {
			executor.shutdown();
		}
	
		if (consumer != null) {
			consumer.shutdown();
		}
	}

	private final static class ConsumerThread implements Runnable {

		private KafkaStream<byte[], byte[]> stream;
		private int threadNumber;
		private int messagesToBeConsumed;
		private CountDownLatch latch;

		public ConsumerThread(KafkaStream<byte[], byte[]> stream, int threadNumber, int messagesToBeConsumedByOneThread, CountDownLatch latch) {
			this.threadNumber = threadNumber;
			this.stream = stream;
			this.messagesToBeConsumed = messagesToBeConsumedByOneThread;
			this.latch = latch;
		}

		public void run() {
			ConsumerIterator<byte[], byte[]> it = stream.iterator();
			int i = 0;
			while (it.hasNext() && i++ < messagesToBeConsumed) {
				System.out.println("Message from thread " + threadNumber + ": " + new String(it.next().message()));
			}
			System.out.println("Shutting down thread: " + threadNumber + ", messages consumed: " + i);
			latch.countDown();
		}
	}
}
