package net.disruptor;

import net.eam.target.SimpleCounter;

import com.lmax.disruptor.EventHandler;

public class LogEntryHandler implements EventHandler<LogEntry> {

	public LogEntryHandler() {
	}

	public void onEvent(final LogEntry logEntry, final long sequence, final boolean endOfBatch) throws Exception {
		SimpleCounter.doIncrement(1);
	}
}
