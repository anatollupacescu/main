package samza.examples.system;

import org.apache.samza.config.Config;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.SystemAdmin;
import org.apache.samza.system.SystemConsumer;
import org.apache.samza.system.SystemFactory;
import org.apache.samza.system.SystemProducer;
import org.apache.samza.util.SinglePartitionWithoutOffsetsSystemAdmin;

public class HazelcastSystemFactory implements SystemFactory {

	@Override
	public SystemConsumer getConsumer(String systemName, Config config, MetricsRegistry registry) {
		String connection = config.get("systems." + systemName + ".connection");
		String topicName = config.get("systems." + systemName + ".topic");
		return new HazelcastSystemConsumer(connection, systemName, topicName);
	}

	@Override
	public SystemProducer getProducer(String systemName, Config config, MetricsRegistry registry) {
		throw new IllegalStateException();
	}

	@Override
	public SystemAdmin getAdmin(String systemName, Config config) {
		return new SinglePartitionWithoutOffsetsSystemAdmin();
	}

}
