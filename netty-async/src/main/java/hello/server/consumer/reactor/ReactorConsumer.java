package hello.server.consumer.reactor;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import reactor.core.Reactor;
import reactor.core.composable.Stream;
import reactor.event.Event;
import reactor.function.Consumer;
import reactor.function.Function;
import reactor.function.Predicate;
import reactor.net.NetChannel;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;

public final class ReactorConsumer implements Consumer<NetChannel<FullHttpRequest, FullHttpResponse>> {

    @Autowired
    Logger log;

    @Autowired
    Reactor reactor;

    @Override
    public void accept(final NetChannel<FullHttpRequest, FullHttpResponse> channel) {
        log.debug("Accepted client");

        Stream<FullHttpRequest> stream = channel.in();

        stream.when(Throwable.class, ev -> {
            channel.send(respond(ev.getMessage(), INTERNAL_SERVER_ERROR));
        });

        Predicate<FullHttpRequest> predicate = new Predicate<FullHttpRequest>() {
            public boolean test(FullHttpRequest req) {
                if (req.getMethod() != HttpMethod.GET) {
                    channel.send(respond(req.getMethod() + " not supported for this URI", BAD_REQUEST));
                    return false;
                }
                return true;
            }
        };

        Function<FullHttpRequest, String> extractName = new Function<FullHttpRequest, String>() {
            public String apply(FullHttpRequest req) {
                final String uri = req.getUri();
                String[] query = uri.split("\\?");
                if (query.length < 2) {
                    return null;
                }
                final Map<String, String> map = Splitter.on('&').trimResults().withKeyValueSeparator("=").split(query[1]);
                return map.get("name");
            }
        };

        Predicate<String> nameNotNull = new Predicate<String>() {
            public boolean test(String name) {
                if (Strings.isNullOrEmpty(name)) {
                    channel.send(respond("'name' parameter expected in query url", BAD_REQUEST));
                    return false;
                }
                return true;
            }
        };

        stream.filter(predicate).map(extractName).filter(nameNotNull).consume(name -> {
            reactor.notify("sayhi", Event.wrap(name), ev -> {
                log.debug("Responding to client...");
                channel.send(respond("Saying hi from reactor to " + name, OK));
            });
        });
    }

    private FullHttpResponse respond(String msg, HttpResponseStatus status) {
        DefaultFullHttpResponse resp = new DefaultFullHttpResponse(HTTP_1_1, status);
        resp.content().writeBytes(msg.getBytes());
        resp.headers().set("Content-Type", "text/plain");
        resp.headers().set("Content-Length", new Integer(resp.content().readableBytes()).toString());
        return resp;
    }
}
