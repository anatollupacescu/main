package mr.camel;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.ImmutableMap;

import mr.Application;
import mr.bean.integration.MyRoute;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class MyThingTest extends CamelTestSupport {

	@Produce(uri = "seda:nowhere")
	protected ProducerTemplate testProducer;

	@Configuration
	public static class TestConfig extends SingleRouteCamelConfiguration {
		@Bean
		public RouteBuilder route() {
			return new MyRoute();
		}
	}

	@Test
	public void testRoute() throws InterruptedException {

		testProducer.sendBodyAndHeaders(new Object(), ImmutableMap.<String, Object> of("type", "unu"));
		testProducer.sendBodyAndHeaders(new Object(), ImmutableMap.<String, Object> of("type", "doi"));

		assertMockEndpointsSatisfied();
	}

}