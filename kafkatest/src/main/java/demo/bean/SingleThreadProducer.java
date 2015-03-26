package demo.bean;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

public class SingleThreadProducer {

	private final Producer<Integer, String> producer;
	private final String topic;

	public SingleThreadProducer(Producer<Integer, String> producer, String topic) {
		super();
		this.producer = producer;
		this.topic = topic;
	}

	public void sendMessages(int count) {
		for (long i = 0; i < count; i++) {
			String msg = "This message is for key - " + i;
			final KeyedMessage<Integer, String> message = new KeyedMessage<Integer, String>(topic, msg);
			producer.send(message);
		}
	}
}
