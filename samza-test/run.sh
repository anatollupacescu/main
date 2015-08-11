mvn clean package -o
tar -xvf ./target/samza-task-0.10.0-dist.tar.gz -C ./target/
target/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/target/config/samza-task.properties
