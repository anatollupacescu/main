package rstest;

import java.util.Date;
import java.util.Properties;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.test.TestingServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RstestApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(RstestApplication.class, args);
	}

	@Value("${zookeeper.connect")
	private String zookr;

	@Autowired
	private Date date;

	@Override
	public void run(String... args) {
		System.out.println(date);
	}

	@Bean
	public Date date() {
		return new Date();
	}

	@Bean
	public TestingServer testServer() throws Exception {
		return new TestingServer(2181);
	}

	@Bean
	public CuratorFramework framework(TestingServer testServer) {
		return CuratorFrameworkFactory.newClient(testServer.getConnectString(), new RetryOneTime(2000));
	}
	
	@Bean
	public KafkaLocalBroker kafkaBroker() {
		Properties p = System.getProperties();
		System.out.println(p);
		return new KafkaLocalBroker();
		
	}
}
