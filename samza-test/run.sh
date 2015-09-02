mvn clean package 
tar -xvf ./target/samza-test-0.10.0-dist.tar.gz -C ./target/
target/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/target/config/samza-task.properties
