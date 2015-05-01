package hello;

import hello.server.consumer.HttpSnoopServerHandler;
import hello.server.consumer.reactor.ReactorConsumer;
import hello.server.consumer.rx.ObservableConsumer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.server.HttpServer;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;
import reactor.event.selector.Selectors;
import reactor.net.NetServer;
import reactor.net.config.ServerSocketOptions;
import reactor.net.netty.NettyServerSocketOptions;
import reactor.net.netty.tcp.NettyTcpServer;
import reactor.net.tcp.spec.TcpServerSpec;
import reactor.spring.context.config.EnableReactor;

@SpringBootApplication
public class NettyServersApp {

	public @Bean Logger log() {
		return LoggerFactory.getLogger(NettyServersApp.class);
	}

	@Configuration
	public static class NettyConfiguration {

		@Value("${local.netty.server.port}")
		private int port;

		@Bean
		public Object trickleServer(NetServer<FullHttpRequest, FullHttpResponse> reactorNettyServer, HttpServer<ByteBuf, ByteBuf> rxNettyServer) {
			CountDownLatch closeLatch = new CountDownLatch(1);
			EventLoopGroup bossGroup = new NioEventLoopGroup(1);
			EventLoopGroup workerGroup = new NioEventLoopGroup();
			try {
				ServerBootstrap b = new ServerBootstrap();
				b.group(bossGroup, workerGroup)
						.channel(NioServerSocketChannel.class)
						.handler(new LoggingHandler(LogLevel.INFO))
						.childHandler(new ChannelInitializer<SocketChannel>() {
							protected void initChannel(SocketChannel ch) throws Exception {
								ch.pipeline().addLast(new HttpRequestDecoder())
										.addLast(new HttpResponseEncoder())
										.addLast(new HttpSnoopServerHandler(closeLatch));
							}
						}).bind(port);

				closeLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				bossGroup.shutdownGracefully();
				workerGroup.shutdownGracefully();
			}

			return new Object();
		}
	}

	@Configuration
	@EnableReactor
	public static class ReactorConfiguration {

		public @Bean Reactor reactor(Environment env, Logger log) {
			Reactor reactor = Reactors.reactor(env, Environment.WORK_QUEUE);
			reactor.on(Selectors.$("sayhi"), (s) -> {
				log.debug("Processing in reactor request from " + s);
			});
			return reactor;
		}

		public @Bean ReactorConsumer reactorConsumer() {
			return new ReactorConsumer();
		}

		public @Bean ReactorConsumer imageConsumer() {
			return new ReactorConsumer();
		}

		@Bean
		public ServerSocketOptions serverSocketOptions() {
			return new NettyServerSocketOptions().pipelineConfigurer(
					pipeline -> pipeline.addLast(new HttpServerCodec())
										.addLast(new HttpObjectAggregator(16 * 1024 * 1024)));
		}

		@Value("${local.reactor.server.port}")
		private int port;

		@Bean
		public NetServer<FullHttpRequest, FullHttpResponse> reactorNettyServer(Environment env, ServerSocketOptions serverSocketOptions, ReactorConsumer reactorConsumer)
				throws InterruptedException {

			NetServer<FullHttpRequest, FullHttpResponse> server = new TcpServerSpec<FullHttpRequest, FullHttpResponse>(NettyTcpServer.class)
					.env(env)
					.listen(port)
					.dispatcher("sync")
					.options(serverSocketOptions)
					.consume(reactorConsumer)
				.get();

			server.start().await();

			return server;
		}
	}

	@Configuration
    public static class RxNettyConfiguration {

        public @Bean ObservableConsumer observableConsumer() {
            return new ObservableConsumer();
        }

        public @Bean HttpServer<ByteBuf, ByteBuf> rxNettyServer(@Value("${local.rx.server.port}") int port) {
            return RxNetty.newHttpServerBuilder(port, observableConsumer()).build().start();
        }
    }

	public static void main(String... args) throws Exception {
		SpringApplication.run(NettyServersApp.class, args);
	}
}
