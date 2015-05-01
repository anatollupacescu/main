package hello.server.consumer.rx;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Strings;

import rx.Observable;

public class ObservableConsumer implements RequestHandler<ByteBuf, ByteBuf> {

    @Autowired
    Logger log;

    @Override
    public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
        log.debug("Responding to client...");
        List<String> name = request.getQueryParameters().get("name");
        if (name == null || name.isEmpty() || Strings.isNullOrEmpty(name.get(0))) {
            response.setStatus(HttpResponseStatus.BAD_REQUEST);
            return response.writeStringAndFlush("'name' parameter expected in query");
        }
        response.setStatus(HttpResponseStatus.OK);
        return response.writeStringAndFlush("Rx says hi to " + name.iterator().next());
    }
}
