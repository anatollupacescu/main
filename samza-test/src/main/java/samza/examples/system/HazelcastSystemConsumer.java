package samza.examples.system;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.samza.Partition;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.util.BlockingEnvelopeMap;
import org.apache.log4j.Logger;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

public class HazelcastSystemConsumer extends BlockingEnvelopeMap {

	private static final Logger log = Logger.getLogger(HazelcastSystemConsumer.class);
	
	private final HazelcastInstance instance;
	private final String systemName;
	private final String topicName;
	
	public HazelcastSystemConsumer(String connection, final String systemName, final String topicName) {
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.getNetworkConfig().addAddress(connection);
		this.instance = HazelcastClient.newHazelcastClient(clientConfig);
		this.systemName = systemName;
		this.topicName = topicName;
	}

	@Override
	public void start() {
		ITopic<String> topic = instance.getTopic(topicName);
		log.debug(String.format("Listening to topic %s", topic.getName()));
		topic.addMessageListener(new MessageListener<String>() {
			@Override
			public void onMessage(Message<String> message) {
				SystemStreamPartition systemStreamPartition = new SystemStreamPartition(systemName, topicName, new Partition(0));
				log.debug(String.format("Sending message to %s:%s", systemName, topicName ));
			    try {
			      put(systemStreamPartition, new IncomingMessageEnvelope(systemStreamPartition, null, null, message.getMessageObject()));
			    } catch (Exception e) {
			      System.err.println(e);
			    }
			}
		});
	}

	@Override
	public void stop() {
		instance.shutdown();
	}

	@Override
	public void register(SystemStreamPartition systemStreamPartition, String offset) {
		super.register(systemStreamPartition, offset);
	}

	@Override
	public Map<SystemStreamPartition, List<IncomingMessageEnvelope>> poll(
			Set<SystemStreamPartition> systemStreamPartitions, long timeout) throws InterruptedException {
		return super.poll(systemStreamPartitions, timeout);
	}

}
