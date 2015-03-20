package rstest;

import java.io.IOException;
import java.util.Random;

import kafka.producer.KeyedMessage;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import test.kafka.MultiBrokerProducer;
import test.kafka.consumer.MultiThreadHLConsumer;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RstestApplication.class)
public class RstestApplicationTests {

	@Autowired
	private TestingServer testServer;
	@Autowired
	private CuratorFramework framework;
	@Autowired
	private KafkaLocalBroker kafkaLocalBroker;
	
	final String topic = "topic";

	@Test
	public void testMultiBrokerProducer() {
		
		MultiThreadHLConsumer consumer = new MultiThreadHLConsumer("localhost:2181", "testgroup", topic);
		consumer.testConsumer();

		MultiBrokerProducer brokerProducer = new MultiBrokerProducer();
		Random random = new Random();

		for (long i = 0; i < 10; i++) {
			Integer key = random.nextInt(255);
			String msg = "This message is for key - " + key;
			final KeyedMessage<Integer, String> message = new KeyedMessage<Integer, String>(topic, msg);
			brokerProducer.getProducer().send(message );
		}
		brokerProducer.getProducer().close();
		consumer.close();
		kafkaLocalBroker.stop();
	}

	@After
	public void stopZookeeper() throws IOException {
		framework.close();
		testServer.stop();
	}
}
