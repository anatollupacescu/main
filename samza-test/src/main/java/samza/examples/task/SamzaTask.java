package samza.examples.task;

import org.apache.log4j.Logger;
import org.apache.samza.Partition;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskCoordinator;

public class SamzaTask implements StreamTask {

	private static final Logger logger = Logger.getLogger(SamzaTask.class);
	private static final SystemStream OUTPUT_STREAM = new SystemStream("kafka", "task-out");

	@Override
	public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
		final Object message = envelope.getMessage();
		if (message == null) {
			logger.info("Null message received, skipping...");
			return;
		}
		SystemStreamPartition systemStreamPartition;
		Partition partition = (systemStreamPartition = envelope.getSystemStreamPartition()) != null ? systemStreamPartition.getPartition() : null;
		int partitionId = partition != null ? partition.getPartitionId() : -1;
		logger.info(String.format("Instance [%s] Received message '%s' from partition %s", this.toString(), message, partitionId));
		final String output = String.format("'%s' is processed by '%s' from partition %s", message, this.toString(), partitionId);
		collector.send(new OutgoingMessageEnvelope(OUTPUT_STREAM, output));
	}
}
