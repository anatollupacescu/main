package demo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import demo.bean.SingleThreadConsumer;
import kafka.server.KafkaServer;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DemoApplication.class)
@ActiveProfiles("consumer")
public class ConsumerTest {

	private @Autowired KafkaServer kafkaBroker;

	private @Autowired Logger logger;

	private @Autowired SingleThreadConsumer consumer;

	public @Value("${kafka.topic}") String topic;

	@Test
	@Ignore
	public void test() {
		assertThat(topic, notNullValue());
		assertThat((byte) 4, equalTo(kafkaBroker.brokerState().currentState()));
		logger.warn("Reading messages from '{}'", topic);
		consumer.readMessages();
	}
}
