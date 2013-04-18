package net.camel;

import java.io.File;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.util.jndi.CamelInitialContextFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class Camel extends RouteBuilder {
	
	private DefaultCamelContext camelContext;
	
	public static void main(String[] args) throws Exception {
		Camel c = new Camel();
		c.configure();
		String content = Files.toString(new File("/tmp/entry.json"), Charsets.UTF_8);
		c.send(content);
	}
	
	public void send(String i) throws Exception {
		camelContext.start();
		ProducerTemplate template = camelContext.createProducerTemplate();
		template.sendBody("direct:increment", i);
		Thread.sleep(6000);
		camelContext.stop();
	}
	
	@Override
	public void configure() throws Exception {
		JndiRegistry jndi = new JndiRegistry();
		CamelInitialContextFactory initialContextFactory = new org.apache.camel.util.jndi.CamelInitialContextFactory();
		Hashtable<?, ?> environment = new Properties();
		Context initialContext = initialContextFactory.getInitialContext(environment);
		jndi.setContext(initialContext);
		
		jndi.bind("convertor", new Bean());
		
		camelContext = new DefaultCamelContext();
		camelContext.setRegistry(jndi);
		camelContext.addRoutes(this);
		camelContext.start();
		
		from("direct:increment").to("bean:convertor?method=convert").beanRef("convertor","print").log("done");
	}
}
