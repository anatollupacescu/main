package mr.f.spring_boot_camel;

import mr.reactor.ReactorRoute;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ReactiveRouteBuilderTest {

	@Produce(uri = "direct:reactor")
	protected ProducerTemplate testProducer;

	@Configuration
	public static class TestConfig extends SingleRouteCamelConfiguration {
		@Bean
		public RouteBuilder route() {
			return new ReactorRoute();
		}
	}

	@Test
	public void testRoute() throws InterruptedException {
		testProducer.requestBody("direct:reactor", "test1");
		testProducer.requestBody("direct:reactor", "test2");
		
//		testProducer.sendBodyAndHeaders(null, ImmutableMap.<String, Object> of("name", "test"));
//		testProducer.sendBodyAndHeaders(null, ImmutableMap.<String, Object> of("name", "test2"));
	}

}