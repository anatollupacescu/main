package net.disruptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

public class BackgroundLogger {

	private final ExecutorService executorService;
	private final Disruptor<LogEntry> disruptor;
	private final RingBuffer<LogEntry> ringBuffer;

	@SuppressWarnings("unchecked")
	public BackgroundLogger() {
		executorService = Executors.newCachedThreadPool();
		disruptor = new Disruptor<LogEntry>(LogEntry.FACTORY, 64, executorService);
		disruptor.handleEventsWith(new LogEntryHandler()); 
		ringBuffer = disruptor.start();
	}

	public void log(String text) {
		final long sequence = ringBuffer.next();
		final LogEntry logEntry = ringBuffer.getPreallocated(sequence);

		logEntry.setText(text);

		ringBuffer.publish(sequence);
	}

	public void stop() {
		disruptor.shutdown();
		executorService.shutdownNow();
	}
}
