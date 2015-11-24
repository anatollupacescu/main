package mr.camel;

import org.apache.camel.rx.ReactiveCamel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.base.Joiner;

import mr.Constants;

public class Rx implements Constants {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("${kafka.host}")
	private String kafkaHost;

	@Value("${kafka.port}")
	private String kafkaPort;

	@Value("${kafka.topic}")
	private String kafkaTopic;

	@Value("${kafka.group}")
	private String kafkaGroup;

	@Value("${kafka.consumer.count}")
	private String kafkaConsumerCount;

	@Value("${zk.host}")
	private String zkHost;

	@Value("${zk.port}")
	private String zkPort;

	@Autowired
	private ReactiveCamel reactiveCamel;

	public void start() {
		String options = Joiner.on("&").join(concat(KAFKA_TOPIC, kafkaTopic), 
				concat(ZOOKEEPER_HOST, zkHost),
				concat(ZOOKEEPER_PORT, zkPort), 
				concat(KAFKA_GROUP_ID, kafkaGroup),
				concat(KAFKA_CONSUMER_COUNT, kafkaConsumerCount));

		String uri = String.format("%s:%s:%s?%s", KAFKA_SCHEMA, kafkaHost, kafkaPort, options);

		reactiveCamel.toObservable(uri)
				// .filter(m -> m.getHeader("foo") != null)
				// .map(m -> "Hello " + m.getBody())
				.forEach(m -> {
					logger.debug("Received '{}'", m.getBody(String.class));
				});
	}

	private String concat(String head, String tail) {
		return head + "=" + tail;
	}
}
