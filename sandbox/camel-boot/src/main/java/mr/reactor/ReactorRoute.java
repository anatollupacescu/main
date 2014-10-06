package mr.reactor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import reactor.core.Reactor;
import reactor.event.Event;

public class ReactorRoute extends RouteBuilder {

	@Autowired
	private Reactor mainReactor;

	@Autowired
	private Logger log;

	@Override
	public void configure() throws Exception {
		from("servlet:///reactor").log("Reactor route enter").process(new ReactorProcessor());
	}

	class ReactorProcessor implements Processor {
		public void process(Exchange exchange) throws Exception {
			String name = exchange.getIn().getHeader("name", String.class);
			for (int i = 0; i < 10; i++) {
				Thread.sleep(100);
				mainReactor.notify("quotes", Event.wrap(name + ":" + i));
				log.info("sent {} : {}", name, i);
			}
		}
	}
}
