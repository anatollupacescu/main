package demo;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.curator.test.TestingServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import demo.bean.SingleThreadConsumer;
import demo.bean.SingleThreadProducer;
import kafka.server.KafkaServer;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DemoApplication.class, args);
		Logger logger = context.getBean(Logger.class);
		logger.warn("Received arguments: {}", Arrays.asList(args));
		String profile = (args == null || args[0] == null) ? "server" : args[0];
		switch (profile) {
		case "producer":
			logger.warn("Ready to send messages, type 'exit' when done");
			try (Scanner scanner = new Scanner(System.in)) {
				SingleThreadProducer producer = context.getBean(SingleThreadProducer.class);
				String topic;
				if (args.length > 1) {
					topic = args[1];
				} else {
					throw new IllegalArgumentException("Second argument should be the topic name");
				}
				for (String message = scanner.nextLine(); !"exit".equals(message);) {
					producer.sendMessage(topic, message, true);
				}
			}
			break;
		case "consumer":
			SingleThreadConsumer consumer = context.getBean(SingleThreadConsumer.class);
			logger.warn("Listening for messages...");
			String topic;
			Integer messageCount;
			if (args.length > 2) {
				topic = args[1];
				messageCount = Integer.valueOf(args[2]);
			} else {
				throw new IllegalArgumentException("Second argument should be the topic name");
			}
			consumer.readMessages(topic, messageCount);
			break;
		case "server":
			logger.warn("Kafka broker starting...");
			KafkaServer kafkaBroker = context.getBean(KafkaServer.class);
			kafkaBroker.startup();
			try (final Scanner scanner = new Scanner(System.in)) {
				logger.warn("Hit 'Enter' to close");
				scanner.nextLine();
				logger.warn("Shutting everything down...");
				kafkaBroker.shutdown();
				kafkaBroker.awaitShutdown();
				TestingServer zk = context.getBean(TestingServer.class);
				zk.stop();
			} catch (IOException e) {
				logger.error("Error shutting down zookeeper", e);
			}
		default:
			logger.error("Profile not provided or not recognized, will exit now");
		}
	}

	public @Bean String profile(@Value("${spring.profiles.active}") String profile) {
		return profile;
	}

	public @Bean Logger logger() {
		return LoggerFactory.getLogger(DemoApplication.class);
	}
}
