package demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;

import org.slf4j.LoggerFactory;
import reactor.Environment;
import reactor.core.Dispatcher;
import reactor.core.DispatcherSupplier;
import reactor.core.processor.RingBufferProcessor;
import reactor.core.processor.RingBufferWorkProcessor;
import reactor.fn.*;
import reactor.fn.tuple.Tuple;
import reactor.fn.tuple.Tuple2;
import reactor.io.buffer.Buffer;
import reactor.io.net.NetSelectors;
import reactor.io.net.NetStreams;
import reactor.io.net.ReactorChannelHandler;
import reactor.io.net.Spec;
import reactor.io.net.http.HttpChannel;
import reactor.io.net.http.HttpServer;
import reactor.rx.BiStreams;
import reactor.rx.Stream;
import reactor.rx.Streams;
import reactor.rx.action.Signal;
import reactor.rx.broadcast.Broadcaster;
import reactor.rx.stream.GroupedStream;

/**
 * Created by anatol on 6/1/15.
 */
public class RectorTest {

    private final org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    static {
        Environment.initializeIfEmpty();
    }

    @Test
    public void testDisp() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        Stream<Long> input = Streams.range(1l, 100l);

        final Processor<Long, Long> publisher = RingBufferProcessor.create("test", 32);
        Stream<Long> list = Streams.wrap(publisher);

        list
                .observeComplete((Void) -> latch.countDown())
                .consume(string -> System.out.println(Thread.currentThread().getName() + "=" + string));

        input.process(publisher);

        log.debug("starting...");

        latch.await(1, TimeUnit.SECONDS);
    }

    @Test
    public void testWordCountJava8() throws InterruptedException {

        final CountDownLatch latch = new CountDownLatch(1);
        final RingBufferProcessor<String> p = RingBufferProcessor.create("test", 32);
        final BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        final DispatcherSupplier dispatcher = Environment.newCachedDispatchers(2);

        new Thread(() -> {
                String line = null;
                while (!"exit".equals(line)) {
                    try {
                        line = console.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    p.onNext(line);
                }
                p.onComplete();
                latch.countDown();
        }, "consoleReader").start();

        Stream<String> s = Streams.wrap(p);

        s.dispatchOn(Environment.sharedDispatcher())

        .flatMap(sentence -> Streams.from(sentence.split(" "))
                        .dispatchOn(dispatcher.get()).log("is this the")
                        .filter(word -> !word.trim().isEmpty())
        )
        
        .map(word -> Tuple.of(word, 1))
        
        .log("same thread?")

        .window(3, TimeUnit.SECONDS)

        .flatMap(words -> BiStreams
                        .reduceByKey(words, (Integer prev, Integer next) -> prev + next)
                        .sort((wordWithCountA, wordWithCountB) -> -wordWithCountA.t2.compareTo(wordWithCountB.t2))
                        .take(10)
                        .finallyDo(tuple2Signal -> log.info("---- window complete! ----"))
        )

        .consume(
                wordWithCount -> log.info(wordWithCount.t1 + ": " + wordWithCount.t2),
                error -> log.error("", error)
        );

        latch.await(1, TimeUnit.MINUTES);
    }

    @Test
    public void testThread() throws InterruptedException {

        final CountDownLatch latch = new CountDownLatch(1);
        final Stream<String> stream = Streams.just("Hello ", "World", "!");
        final Dispatcher rbd = Environment.cachedDispatcher();
        final DispatcherSupplier dispatcher = Environment.newCachedDispatchers(2);

        stream

        .dispatchOn(rbd)

        .partition(2)

        .flatMap(subStream -> 
            subStream
                .dispatchOn(dispatcher.get())
                .log("where am I")
                .map(String::toUpperCase))

        .dispatchOn(rbd)

        .observeComplete(ignore -> {
            log.debug("done");
            latch.countDown();
        })

        .buffer(3)

        .consume(list -> {
            log.debug(list.toString());
        });

        latch.await(10, TimeUnit.SECONDS);
        Environment.terminate();
    }

    @Test
    public void testCountWords() throws InterruptedException, IOException {
        final CountDownLatch latch = new CountDownLatch(1);
        final RingBufferProcessor<String> p = RingBufferProcessor.create("test", 32);

        new Thread(new Runnable() {
            final BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            public void run() {
                String line = null;
                while (!"exit".equals(line)) {
                    try {
                        line = console.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    p.onNext(line);
                }
                p.onComplete();
                latch.countDown();
            }
        }, "consoleReader").start();

        Stream<String> s = Streams.wrap(p);
        s.dispatchOn(Environment.sharedDispatcher())
        .flatMap(new Function<String, Publisher<?>>() {
            @Override
            public Publisher<?> apply(String sentence) {
                return Streams.from(sentence.split(" "))
                        .dispatchOn(Environment.cachedDispatcher())
                        .filter(new Predicate<String>() {
                            public boolean test(String word) {
                                return !word.trim().isEmpty();
                            }
                        });
            }
        })
        .map(new Function<Object, Tuple2<String, Integer>>() {
            public Tuple2<String, Integer> apply(Object word) {
                return Tuple.of((String) word, 1);
            }
        })
        .window(3, TimeUnit.SECONDS)
        .flatMap(new Function<Stream<Tuple2<String, Integer>>, Publisher<?>>() {
            public Publisher<?> apply(Stream<Tuple2<String, Integer>> words) {
                return BiStreams.reduceByKey(words, new BiFunction<Integer, Integer, Integer>() {
                            public Integer apply(Integer prev, Integer next) {
                                return prev + next;
                            }
                        }) 
                        .sort(new Comparator<Tuple2<String, Integer>>() {
                            public int compare(Tuple2<String, Integer> wordWithCountA, Tuple2<String, Integer> wordWithCountB) {
                                return -wordWithCountA.t2.compareTo(wordWithCountB.t2);
                            }
                        })
                        .take(10)
                        .finallyDo(new Consumer<Signal<Tuple2<String, Integer>>>() {
                            public void accept(Signal<Tuple2<String, Integer>> tuple2Signal) {
                                log.info("---- window complete! ----");
                            }
                });
            }
        })
        .consume(new Consumer<Object>() {
                public void accept(Object wordWithCountObj) {
                    Tuple2<String, Integer> wordWithCount = (Tuple2<String, Integer>) wordWithCountObj;
                    log.info(wordWithCount.t1 + ": " + wordWithCount.t2);
                }
            }, new Consumer<Throwable>() {
                public void accept(Throwable error) {
                    log.error("", error);
                }
        });
        latch.await(1, TimeUnit.MINUTES);
    }

    @Test
    public void processorTest() throws InterruptedException {

        final CountDownLatch latch = new CountDownLatch(1);
        RingBufferProcessor<Long> p = RingBufferProcessor.create("test", 32);

        Streams.range(1, 10).process(p);

        Stream<Long> s = Streams.wrap(p);

        s.consume(new Consumer<Long>() {
            @Override
            public void accept(Long integer) {
                log.info(Thread.currentThread() + " data=" + integer);
            }
        });

        s.consume(new Consumer<Long>() {
            @Override
            public void accept(Long integer) {
                log.info(Thread.currentThread() + " data=" + integer);
            }
        });

        latch.await(1, TimeUnit.SECONDS);
    }

    @Test
    public void workProcessorTest() throws InterruptedException {

        final CountDownLatch latch = new CountDownLatch(1);

        RingBufferWorkProcessor<Long> p = RingBufferWorkProcessor.create("test", 32);

        Stream<Long> stream = Streams.wrap(p);
        stream.consume(new Consumer<Long>() {
            @Override
            public void accept(Long integer) {
                log.info(Thread.currentThread() + " data=" + integer);
            }
        });
        stream.consume(new Consumer<Long>() {
            @Override
            public void accept(Long integer) {
                log.info(Thread.currentThread() + " data=" + integer);
            }
        });

        Stream<Long> input = Streams.range(1, 10);
        input.process(p);

        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    public void test2() throws InterruptedException {
        final DispatcherSupplier supplier1 = Environment.newCachedDispatchers(2, "groupByPool");
        final DispatcherSupplier supplier2 = Environment.newCachedDispatchers(5, "partitionPool");

        Streams.range(1, 10)

        .groupBy(new Function<Long, Boolean>() {
            public Boolean apply(Long n) {
                return n % 2 == 0;
            }
        })

        .flatMap(new Function<GroupedStream<Boolean, Long>, Publisher<?>>() {
            @Override
            public Publisher<?> apply(GroupedStream<Boolean, Long> stream) {
                return stream.dispatchOn(supplier1.get()).log("groupBy");
            }
        })

        .partition(6)

        .flatMap(new Function<GroupedStream<Integer, Object>, Publisher<?>>() {
            @Override
            public Publisher<?> apply(GroupedStream<Integer, Object> stream) {
                return stream.dispatchOn(supplier2.get()).log("partition");
            }
        })

        .observe(new Consumer<Object>() {
            public void accept(Object o) {
                log.info(String.format("Thread %s observing value %d", Thread.currentThread().getName(), o));
            }
        })

        .dispatchOn(Environment.sharedDispatcher())

        .log("join").consume();

        new CountDownLatch(1).await(1, TimeUnit.SECONDS);
    }

    @Test
    public void test1() {
        Processor<String, String> processor = RingBufferProcessor.create("test", 32);
        Stream<String> st1 = Streams.just("Hello ");
        Stream<String> st2 = Streams.just("World ");
        Stream<String> st3 = Streams.wrap(processor);

        Streams.concat(st1, st2, st3).reduce(new BiFunction<String, String, String>() {
            public String apply(String prev, String next) {
                return prev + next;
            }
        }).consume(new Consumer<String>() {
            public void accept(String s) {
                log.info("%s greeting = %s%n", Thread.currentThread(), s);
            }
        });

        processor.onNext("!");
        processor.onComplete();
    }

    @Test
    public void test() throws InterruptedException {
        final Broadcaster<String> logger = Broadcaster.create(Environment.sharedDispatcher());
        HttpServer<Buffer, Buffer> httpServer = NetStreams.httpServer(initFunc());
        httpServer.route(NetSelectors.get("/name/{name}"), new ReactorChannelHandler<Buffer, Buffer, HttpChannel<Buffer, Buffer>>() {
            @Override
            public Publisher<Void> apply(HttpChannel<Buffer, Buffer> stream) {
                final String name = stream.param("name");
                logger.onNext(name);
                return stream.writeWith(Streams.just(Buffer.wrap("hi " + name)));
            }
        }).route(NetSelectors.get("/value/{value}"), new ReactorChannelHandler<Buffer, Buffer, HttpChannel<Buffer, Buffer>>() {
            @Override
            public Publisher<Void> apply(HttpChannel<Buffer, Buffer> stream) {
                final String name = stream.param("value");
                logger.onNext(name);
                return stream.writeWith(Streams.just(Buffer.wrap("val " + name)));
            }
        }).start();

        logger.consume(new Consumer<String>() {
            public void accept(String s) {
                log.info(Thread.currentThread().getName() + " # " + s);
            }
        });

        new CountDownLatch(1).await(1, TimeUnit.SECONDS);
        httpServer.shutdown();
    }

    static Function<? super Spec.HttpServerSpec<Buffer, Buffer>, ? extends Spec.HttpServerSpec<Buffer, Buffer>> initFunc() {
        return new Function<Spec.HttpServerSpec<Buffer, Buffer>, Spec.HttpServerSpec<Buffer, Buffer>>() {
            @Override
            public Spec.HttpServerSpec<Buffer, Buffer> apply(Spec.HttpServerSpec<Buffer, Buffer> bufferBufferHttpServerSpec) {
                return bufferBufferHttpServerSpec.synchronousDispatcher().listen(8081);
            }
        };
    }
}
