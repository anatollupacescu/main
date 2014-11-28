package mr;

import mr.odata.HelloMethods;
import mr.reactor.FlushBean;
import mr.reactor.ReactorQueues;
import mr.reactor.ReactorRoute;
import mr.serioja.FtpUploadRoute;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.spring.SpringCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.spring.context.config.EnableReactor;

@EnableAutoConfiguration
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Configuration
	@ComponentScan
	public static class FunctionalContextConfiguration {
		@Bean
		public HelloMethods helloMethods() {
			return new HelloMethods();
		}
	}
	
	@Configuration
	@EnableReactor
	@ComponentScan
	public static class ReactorConfiguration {

		@Bean
		public Reactor reactor(Environment env) {
			return env.getRootReactor();
		}

		@Bean
		public Logger log() {
			return LoggerFactory.getLogger(Application.class);
		}
		
		@Bean
		public RestTemplate restTemplate() {
			return new RestTemplate();
		}
	}

	@Configuration
	@ComponentScan
	public static class CamelConfiguration {

		private static final String CAMEL_URL_MAPPING = "/camel/*";
		private static final String CAMEL_SERVLET_NAME = "CamelServlet";

		@Bean
		public ServletRegistrationBean servletRegistrationBean() {
			ServletRegistrationBean registration = new ServletRegistrationBean(new CamelHttpTransportServlet(),
					CAMEL_URL_MAPPING);
			registration.setName(CAMEL_SERVLET_NAME);
			return registration;
		}

		@Bean
		public SpringCamelContext camelContext(ApplicationContext applicationContext) throws Exception {
			SpringCamelContext camelContext = new SpringCamelContext(applicationContext);
			camelContext.addRoutes(reactorRoute());
			camelContext.addRoutes(ftpRoute());
			return camelContext;
		}

		@Bean
		public RouteBuilder reactorRoute() {
			return new ReactorRoute();
		}
		
		@Bean
		public RouteBuilder ftpRoute() {
			return new FtpUploadRoute();
		}
	}

	@Bean
	public FlushBean flushBean() {
		return new FlushBean();
	}
	
	@Bean
	public ReactorQueues queues() {
		return new ReactorQueues();
	}
}