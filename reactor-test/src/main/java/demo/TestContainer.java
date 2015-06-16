package demo;

import com.google.common.base.Optional;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestContainer<E> {

    private final ExecutorService queueManager = Executors.newSingleThreadExecutor();
    private final ExecutorService workerPool = Executors.newCachedThreadPool();

    private final Queue<E> queue = new LinkedList<E>();
    private final ExpensiveObjectCreator<E> creator;

    public TestContainer(ExpensiveObjectCreator<E> wrapper) {
        this.creator = wrapper;
    }

    public void execute(final String req) throws InterruptedException {

        queueManager.execute(new Runnable() {
            public void run() {
                final E e = Optional.fromNullable(queue.poll()).or(creator.create());
                workerPool.execute(new Runnable() {
                    public void run() {
                        System.out.println(String.format("long running job with '%s' on '%s'", e, req));
                        queueManager.execute(new Runnable() {
                            public void run() {
                                queue.add(e);
                            }
                        });
                    }
                });
            }
        });
    }

    public void shutdown() {
        workerPool.shutdownNow();
        queueManager.shutdownNow();
    }

    public interface ExpensiveObjectCreator<T> {
        T create();
    }
}
