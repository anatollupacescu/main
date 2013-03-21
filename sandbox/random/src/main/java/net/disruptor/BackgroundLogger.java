package net.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundLogger
{
    private static final int ENTRIES = 64;

    private final ExecutorService executorService;
    private final Disruptor<LogEntry> disruptor;
    private final RingBuffer<LogEntry> ringBuffer;

    public BackgroundLogger()
    {
        executorService = Executors.newCachedThreadPool();
        disruptor = new Disruptor<LogEntry>(LogEntry.FACTORY, ENTRIES, executorService);
        disruptor.handleEventsWith(new LogEntryHandler());
        disruptor.start();
        ringBuffer = disruptor.getRingBuffer();
    }

    public void log(String text)
    {
        final long sequence = ringBuffer.next();
        final LogEntry logEntry = ringBuffer.getPublished(sequence);

        logEntry.time = System.currentTimeMillis();
        logEntry.level = 1;
        logEntry.text = text;

        ringBuffer.publish(sequence);
    }

    public void stop()
    {
        disruptor.shutdown();
        executorService.shutdownNow();
    }
}


