package demo;

import java.util.Properties;
import java.util.Scanner;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;

public class ProducerTest {

	final String topic = "test2";
	
	@Test
	public void producerTest1() {
		final Properties properties = new Properties(); 
		final String serializer = "org.apache.kafka.common.serialization.StringSerializer";
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "be4a155a7302:9092");
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializer);
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, serializer);
		properties.put(ProducerConfig.RETRIES_CONFIG, "1");
		KafkaProducer<Integer, String> producer = new KafkaProducer<Integer, String>(properties);
		Scanner scanner = new Scanner(System.in);
		String msg = null;
		while(!"exit".equals(msg)) {
			msg = scanner.nextLine();
			ProducerRecord<Integer, String> record = new ProducerRecord<Integer, String>(topic, msg);
			producer.send(record);
		}
		scanner.close();
		producer.close();
	}
}
