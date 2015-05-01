package hello.server.consumer;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import hello.server.consumer.trickle.TrickleGraphBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import com.google.common.base.Strings;
import com.spotify.trickle.Graph;

public class HttpSnoopServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final String NEW_LINE = "\r\n";

    private HttpRequest request;

    /** Buffer that stores the response content */
    private final StringBuilder buf = new StringBuilder();

    private final Graph<String> graph;
    private final CountDownLatch latch;

    public HttpSnoopServerHandler(CountDownLatch closeLatch) throws Exception {
        this.latch = closeLatch;
        this.graph = TrickleGraphBuilder.getGraph();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {

        if (latch.getCount() == 0) {
            return;
        }

        if (msg instanceof HttpRequest) {
            HttpRequest request = this.request = (HttpRequest) msg;

            if (request.getUri().endsWith("/shutdown")) {
                DefaultFullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                resp.content().writeBytes("Goodbuy!".getBytes());
                resp.headers().set("Content-Type", "text/plain");
                resp.headers().set("Content-Length", new Integer(resp.content().readableBytes()).toString());
                ctx.write(resp);
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                latch.countDown();
                return;
            }

            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
            Map<String, List<String>> params = queryStringDecoder.parameters();
            List<String> name = params.get("name");

            if (name == null || name.isEmpty() || Strings.isNullOrEmpty(name.get(0))) {
                buf.append("'name' parameter expected in the uri query\r\n");
            } else {
                String s = name.iterator().next();
                try {
                    String trickleResponse = graph.bind(TrickleGraphBuilder.NAME, s).bind(TrickleGraphBuilder.GREETING, "Hello ").run().get();
                    buf.append(trickleResponse);
                    buf.append(NEW_LINE);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            appendDecoderResult(buf, request);
        }

        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;

            ByteBuf content = httpContent.content();
            if (content.isReadable()) {
                buf.append("CONTENT: ");
                final String str = content.toString(CharsetUtil.UTF_8);
                buf.append(str);
                buf.append(NEW_LINE);
                appendDecoderResult(buf, request);
            }

            if (msg instanceof LastHttpContent) {
                buf.append("Process by trickle library\r\n");

                LastHttpContent trailer = (LastHttpContent) msg;
                if (!trailer.trailingHeaders().isEmpty()) {
                    buf.append(NEW_LINE);
                    for (CharSequence name : trailer.trailingHeaders().names()) {
                        for (CharSequence value : trailer.trailingHeaders().getAll(name)) {
                            buf.append("TRAILING HEADER: ");
                            buf.append(name).append(" = ").append(value).append(NEW_LINE);
                        }
                    }
                    buf.append(NEW_LINE);
                }

                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, 
                        trailer.getDecoderResult().isSuccess() ? OK : BAD_REQUEST, 
                        Unpooled.copiedBuffer(buf.toString(),
                        CharsetUtil.UTF_8));

                response.headers().set("Content-Type", "text/plain; charset=UTF-8");
                ctx.write(response);
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                    .addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    private static void appendDecoderResult(StringBuilder buf, HttpObject o) {
        DecoderResult result = o.getDecoderResult();
        if (result.isSuccess()) {
            return;
        }

        buf.append(".. WITH DECODER FAILURE: ");
        buf.append(result.cause());
        buf.append(NEW_LINE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
}
