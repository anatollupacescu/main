package mr.reactor;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import reactor.core.Reactor;
import reactor.event.Event;
import reactor.event.selector.Selectors;
import reactor.function.Consumer;

public class QuotesHandlerBean {

	@Autowired
	private Logger log;

	@Autowired
	public void configureQuotesConsumer(Reactor reactor) {
		reactor.on(Selectors.$("quotes"), new Consumer<Event<String>>() {
			@Override
			public void accept(Event<String> t) {
				log.info("accepted {}", t.getData());
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
