package mr.bean.integration;

import java.util.Map;

import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.loadbalancer.LoadBalancerSupport;
import org.apache.camel.rx.ObservableBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import rx.Observable;

public class MyRoute extends RouteBuilder {

	private static final Logger log = LoggerFactory.getLogger(MyRoute.class);
	
	private final Map<String, Integer> state = Maps.newHashMap();
	
	@Override
	public void configure() throws Exception {
		ObservableBody<String> body = new ObservableBody<String>(String.class) {
			protected void configure(Observable<String> observable) {
				observable.reduce((str1, str2) -> {
					Integer existing = state.get(str2);
					Integer newValue = 1;
					if(existing != null) {
						newValue = existing + 1;
					}
					if(existing > 2) {
						System.out.println(str2 + " has reached max");
						newValue = 1;
					}
					state.put(str2, newValue);
					return null;
				});
			}
		};
		from("seda:nowhere").process(new Processor() {
			public void process(Exchange exchange) throws Exception {
				log.debug("before");
			}}).loadBalance().sticky(header("type")).to("seda:unu").to("seda:doi");
		
		from("seda:unu").process(new Processor() {
			public void process(Exchange exchange) throws Exception {
				log.debug("In unu {}", exchange.getIn().getHeader("type"));
			}
		});
		from("seda:doi").process(new Processor() {
			public void process(Exchange exchange) throws Exception {
				log.debug("In doi {}", exchange.getIn().getHeader("type"));
			}
		});
	}

    private static class MyLoadBalancer extends LoadBalancerSupport {

        public boolean process(Exchange exchange, AsyncCallback callback) {
            String body = exchange.getIn().getBody(String.class);
            try {
                if ("x".equals(body)) {
                    getProcessors().get(0).process(exchange);
                } else if ("y".equals(body)) {
                    getProcessors().get(1).process(exchange);
                } else {
                    getProcessors().get(2).process(exchange);
                }
            } catch (Throwable e) {
                exchange.setException(e);
            }
            callback.done(true);
            return true;
        }
    }
}