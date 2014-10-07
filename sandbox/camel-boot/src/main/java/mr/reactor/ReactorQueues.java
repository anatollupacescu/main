package mr.reactor;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import reactor.core.composable.Deferred;
import reactor.core.composable.Stream;
import reactor.event.Event;
import reactor.spring.context.annotation.Consumer;
import reactor.spring.context.annotation.ReplyTo;
import reactor.spring.context.annotation.Selector;
import reactor.tuple.Tuple2;

@Consumer
public class ReactorQueues {

	@Autowired
	private Logger log;

	@Selector(value = "quotes", reactor = "@reactor")
	@ReplyTo("flush")
	public String quotes(Event<Tuple2<String, Deferred<String, Stream<String>>>> event) {
		log.info("quotes accepted {}", event.getData().getT1());
		Tuple2<String, Deferred<String, Stream<String>>> t2 = event.getData();
		t2.getT2().accept(t2.getT1() + ":quote");
		return event.getData().getT1();
	}

	@Selector(value = "faqs", reactor = "@reactor")
	public void faqs(Event<Tuple2<String, Deferred<String, Stream<String>>>> event) {
		log.info("faqs accepted {}", event.getData().getT1());
		Tuple2<String, Deferred<String, Stream<String>>> t2 = event.getData();
		t2.getT2().accept(t2.getT1() + ":faq");
	}
}