package tmp;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class Test {

	final static Logger log = Logger.getAnonymousLogger();

	final static String ftp_client = "ftp://admin:admin@localhost:21/out/?fileName=${file:name}&tempFileName=${file:onlyname}.part&stepwise=false";
	final static String ftp_server = ftp_client + "&delay=5s&move=done";

	public static void main(String[] args) throws Exception {

		CamelContext context = new DefaultCamelContext();
		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from(ftp_server).to("file:inputdir/download").log("Finished upload");
			}
		});
		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("file:inputdir/upload?moveFailed=../error").log("Uploading file").to(ftp_client).end();
			}
		});
		log.info("starting context");
		context.start();

		new CountDownLatch(1).await();
	}

}
