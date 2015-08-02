package org.kafka.tool;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Scanner;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.kafka.tool.bean.SingleThreadProducer;
import kafka.server.KafkaServer;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = KafkaToolApplication.class)
@ActiveProfiles("producer")
public class ProducerTest {

	private @Autowired KafkaServer kafkaBroker;

	private @Autowired Logger logger;

	private @Autowired SingleThreadProducer producer;
	
	@Test
	@Ignore
	public void producerTest1() {
		assertThat((byte) 4, equalTo(kafkaBroker.brokerState().currentState()));
		logger.warn("Sending messages to topic '{}', type 'exit' when done");
		try (Scanner scanner = new Scanner(System.in)) {
			for (String message = scanner.nextLine(); !"exit".equals(message);) {
				producer.sendMessage("topic", message);
			}
		}
	}
}
