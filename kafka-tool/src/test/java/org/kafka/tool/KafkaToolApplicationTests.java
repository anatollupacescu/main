package org.kafka.tool;

import kafka.server.KafkaServer;
import org.apache.curator.test.TestingServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kafka.tool.bean.SingleThreadProducer;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Scanner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = KafkaToolApplication.class)
@ActiveProfiles("server")
public class KafkaToolApplicationTests {

    private @Autowired KafkaServer kafkaBroker;

    private @Autowired TestingServer zk;

	@Test
	public void contextLoads() throws IOException {
        kafkaBroker.startup();
        assertThat((byte) 4, is(equalTo(kafkaBroker.brokerState().currentState())));
        kafkaBroker.shutdown();
        kafkaBroker.awaitShutdown();
        assertThat((byte) 0, is(equalTo(kafkaBroker.brokerState().currentState())));
        zk.stop();
	}

}
