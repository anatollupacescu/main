package demo;

import java.util.logging.Logger;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import demo.bean.MultiThreadConsumer;
import demo.bean.SingleThreadProducer;
import kafka.server.KafkaServer;

@SpringBootApplication
public class DemoApplication {

	private static final Logger log = Logger.getAnonymousLogger();

	private static final int messageCount = 10;

	public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext context = SpringApplication.run(DemoApplication.class, args);
        MultiThreadConsumer consumerTester = context.getBean(MultiThreadConsumer.class);
        KafkaServer kafkaServer = context.getBean(KafkaServer.class);
        SingleThreadProducer producer = context.getBean(SingleThreadProducer.class);
        CuratorFramework framework = context.getBean(CuratorFramework.class);

        log.info("Kafka starting...");
        kafkaServer.startup();
        
        log.info("Starting consumer...");
        consumerTester.testConsumer();
        
        log.info("Publishing messages...");
		producer.sendMessages(1);
		
		log.info("Shutting down...");
		consumerTester.close();
		kafkaServer.shutdown();
		framework.close();
    }
}
