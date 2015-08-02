package org.kafka.tool.config;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.test.TestingServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;

@Configuration
@Profile("server")
public class ServerConfiguration {
	
	@Bean
	public TestingServer testServer(@Value("${zookeeper.port}") int zookeeperPort) throws Exception {
		return new TestingServer(zookeeperPort);
	}

	@Bean
	public CuratorFramework framework(TestingServer testServer) {
		return CuratorFrameworkFactory.newClient(testServer.getConnectString(), new RetryOneTime(2000));
	}

	@Bean
	public KafkaServer kafkaServer(
			@Value("${kafka.zookeeper.connect}") String zookeeperConnect,
			@Value("${kafka.broker.id}") String brokerId,
			CuratorFramework framework) throws Exception {
		framework.blockUntilConnected(1, TimeUnit.SECONDS);
		Properties properties = new Properties();
		properties.put("zookeeper.connect", zookeeperConnect);
		properties.put("broker.id", brokerId);
		KafkaConfig kafkaConfig = new KafkaConfig(properties);
		return new KafkaServer(kafkaConfig, new kafka.utils.Time() {
			public void sleep(long ms) {}
			public long nanoseconds() {	return 0;}
			public long milliseconds() { return 0; }
		});
	}
}
