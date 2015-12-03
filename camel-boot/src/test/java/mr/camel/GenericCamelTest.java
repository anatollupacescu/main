package mr.camel;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class GenericCamelTest extends CamelTestSupport {

	@EndpointInject(uri = "mock:result")
	public MockEndpoint resultEndpoint;

	@Produce(uri = "direct:input")
	public ProducerTemplate testProducer;

	@Test
	public void testRoute() throws InterruptedException {
		resultEndpoint.expectedBodiesReceived("converted jora");
		testProducer.sendBody("direct:input", "jora");
		assertMockEndpointsSatisfied();
	}

	@Override
	protected RouteBuilder createRouteBuilder() {
		return new RouteBuilder() {
			private Helper helper = new Helper();
			public void configure() throws Exception {
				from("direct:input").bean(helper, "convert").to("mock:result");
			}
		};
	}
}
