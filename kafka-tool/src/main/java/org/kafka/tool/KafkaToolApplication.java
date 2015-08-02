package org.kafka.tool;

import kafka.server.KafkaServer;
import org.apache.curator.test.TestingServer;
import org.kafka.tool.bean.SingleThreadConsumer;
import org.kafka.tool.bean.SingleThreadProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.Scanner;

@SpringBootApplication
public class KafkaToolApplication {

    private static final Logger logger = LoggerFactory.getLogger(KafkaToolApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(KafkaToolApplication.class, args);
        String profile = context.getBean("profile", String.class);
        logger.debug("Running profile: {}", profile);
        switch (profile) {
            case "producer":
                logger.warn("Ready to send messages, type 'exit' when done");
                try (Scanner scanner = new Scanner(System.in)) {
                    SingleThreadProducer producer = context.getBean(SingleThreadProducer.class);
                    String message = null;
                    for (; !"exit".equals(message); message = scanner.nextLine()) {
                        producer.sendMessage(message);
                    }
                    producer.shutdown();
                }
                break;
            case "consumer":
                SingleThreadConsumer consumer = context.getBean(SingleThreadConsumer.class);
                consumer.readMessages(logger);
                break;
            case "server":
                logger.debug("Kafka broker starting...");
                KafkaServer kafkaBroker = context.getBean(KafkaServer.class);
                kafkaBroker.startup();
                try (final Scanner scanner = new Scanner(System.in)) {
                    logger.debug("Hit 'Enter' to close");
                    scanner.nextLine();
                    logger.warn("Shutting everything down...");
                    kafkaBroker.shutdown();
                    kafkaBroker.awaitShutdown();
                    TestingServer zk = context.getBean(TestingServer.class);
                    zk.stop();
                    logger.debug("Done");
                } catch (IOException e) {
                    logger.error("Error shutting down zookeeper", e);
                }
                break;
            default:
                logger.error("Profile not provided or not recognized, will exit now");
        }
    }

    public @Bean String profile(@Value("${spring.profiles.active}") String profile) {
        return profile;
    }
}
