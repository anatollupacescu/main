package mr;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import mr.bean.integration.MyRoute;
import mr.serioja.FtpUploadRoute;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Configuration
	public static class CamelConfiguration {

		private static final String CAMEL_URL_MAPPING = "/camel/*";
		private static final String CAMEL_SERVLET_NAME = "CamelServlet";

		@Bean
		public ServletRegistrationBean servletRegistrationBean() {
			ServletRegistrationBean registration = new ServletRegistrationBean(new CamelHttpTransportServlet(),	CAMEL_URL_MAPPING);
			registration.setName(CAMEL_SERVLET_NAME);
			return registration;
		}

		@Bean
		public SpringCamelContext camelContext(ApplicationContext applicationContext) throws Exception {
			SpringCamelContext camelContext = new SpringCamelContext(applicationContext);
			camelContext.addRoutes(ftpRoute());
			camelContext.addRoutes(myRoute());
			return camelContext;
		}

		@Bean
		public RouteBuilder ftpRoute() {
			return new FtpUploadRoute();
		}
		
		@Bean
		public RouteBuilder myRoute() {
			return new MyRoute();
		}
	}
}