package org.kafka.tool;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.Scanner;

import org.apache.curator.test.TestingServer;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import kafka.server.KafkaServer;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = KafkaToolApplication.class)
@ActiveProfiles("server")
public class ServerTest {

	private @Autowired KafkaServer kafkaBroker;

	private @Autowired TestingServer zk;
	
	private @Autowired Logger logger;

	@Test
	@Ignore
	public void test() throws IOException {
		logger.warn("Kafka broker starting...");
		kafkaBroker.startup();
		assertThat((byte) 4, is(equalTo(kafkaBroker.brokerState().currentState())));
		try (final Scanner scanner = new Scanner(System.in)) {
			logger.warn("Hit 'Enter' to close");
			scanner.nextLine();
		}
		logger.warn("Shutting everything down...");
		kafkaBroker.shutdown();
		kafkaBroker.awaitShutdown();
		assertThat((byte) 0, is(equalTo(kafkaBroker.brokerState().currentState())));
		zk.stop();
	}
}
