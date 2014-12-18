package mr.reactor;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.composable.Deferred;
import reactor.core.composable.Stream;
import reactor.core.composable.spec.Streams;
import reactor.event.Event;
import reactor.function.Consumer;
import reactor.tuple.Tuple;

public class ReactorRoute extends RouteBuilder {

	@Autowired
	private Reactor reactor;

	@Autowired
	private Logger log;

	@Autowired
	private Environment env;

	@Override
	public void configure() throws Exception {
		from("direct:reactor").log("Reactor route enter").process(new ReactorProcessor());
	}

	class ReactorProcessor implements Processor {

		public void process(Exchange exchange) throws InterruptedException {

			String name = exchange.getIn().getBody(String.class);

			final Deferred<String, Stream<String>> deferred = Streams.<String> defer(env);

			final Stream<String> stream = deferred.compose();

			final CountDownLatch latch = new CountDownLatch(1);

			stream.collect(2).consume(new Consumer<List<String>>() {
				@Override
				public void accept(List<String> t) {
					log.info("Consumed {}", t);
					exchange.getOut().setBody(t);
					latch.countDown();
				}
			});

			reactor.notify("quotes", Event.wrap(Tuple.of(name, deferred)));

			reactor.notify("faqs", Event.wrap(Tuple.of(name, deferred)));

			log.info("Waiting in thread {}", Thread.currentThread().getName());
			
			latch.await(3, TimeUnit.SECONDS);
		}
	}
}