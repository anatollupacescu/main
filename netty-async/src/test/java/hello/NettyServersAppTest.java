package hello;

import io.reactivex.netty.RxNetty;

import java.nio.charset.Charset;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.IntStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServersAppTest {

    private static final Logger log = LoggerFactory.getLogger(NettyServersAppTest.class);

    private final int port = 8082;
    
    @Test
    public void test() {
        
        log.debug("Start");
        
        IntStream.iterate(0, n -> n + 1)
                .limit(10)
                .parallel()
                .forEach(
                        (i) -> {
                            Instant start = Instant.now();
                            RxNetty.createHttpGet(String.format("http://localhost:%s/?name=test%s", port, i))
                                    .flatMap(response -> response.getContent())
                                    .map(data -> "Client => " + data.toString(Charset.defaultCharset())).toBlocking()
                                    .forEach((s) -> log.debug(s));
                            long gap = ChronoUnit.MILLIS.between(start, Instant.now());
                            log.debug(String.format("Time spent for request %s: %s", i, Long.valueOf(gap).toString()));
                        });
        
        log.debug("End");
    }
}
