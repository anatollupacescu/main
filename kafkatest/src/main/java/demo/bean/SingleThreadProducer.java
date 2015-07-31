package demo.bean;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

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
			//final KeyedMessage<Integer, String> message = new KeyedMessage<Integer, String>(topic, msg);
			final ProducerRecord<Integer, String> record = new ProducerRecord<>(topic, msg);
			producer.send(record);
		}
	}
}
