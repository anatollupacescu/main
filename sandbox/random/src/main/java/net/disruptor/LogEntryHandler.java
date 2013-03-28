package net.disruptor;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.lmax.disruptor.EventHandler;

public class LogEntryHandler implements EventHandler<LogEntry> {

	private final static Logger log = Logger.getAnonymousLogger();
	
	public LogEntryHandler() {
	}

	public void onEvent(final LogEntry logEntry, final long sequence, final boolean endOfBatch) throws Exception {
		log.log(Level.INFO, "Received entry with text {0} sequence {1} endOfBatch {2}", new Object[] { logEntry.getText(), sequence, endOfBatch });
		Thread.sleep(1000);
		log.log(Level.INFO, "Exiting: {0}", new Object[] { logEntry.getText() });
	}
}
