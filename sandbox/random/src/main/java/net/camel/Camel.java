package net.camel;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.spi.Registry;
import org.apache.camel.util.jndi.CamelInitialContextFactory;

public class Camel extends RouteBuilder {
	
	private DefaultCamelContext camelContext;
	
	public static void main(String[] args) throws Exception {
		Camel c = new Camel();
		c.configure();
		c.send(4);
	}
	
	public void send(Integer i) throws Exception {
		camelContext.start();
		ProducerTemplate template = camelContext.createProducerTemplate();
		template.sendBody("direct:increment", new Integer(3));
		Thread.sleep(6000);
		camelContext.stop();
	}
	
	private Registry createRegistry() throws NamingException {
        JndiRegistry jndi = new JndiRegistry();
        CamelInitialContextFactory initialContextFactory = new org.apache.camel.util.jndi.CamelInitialContextFactory();
        Hashtable<?, ?> environment = new Properties();
        Context initialContext = initialContextFactory.getInitialContext(environment);
		jndi.setContext(initialContext);
        jndi.bind("incrementor", new Bean());
        return jndi;
	}

	@Override
	public void configure() throws Exception {
		camelContext = new DefaultCamelContext();
		try {
			camelContext.setRegistry(createRegistry());
			camelContext.addRoutes(this);
			camelContext.start();
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		from("direct:increment").to("bean:incrementor?method=go").log("done");
	}
}
