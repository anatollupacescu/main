package mr.camel;

import org.apache.camel.builder.RouteBuilder;

/**
 * Created by anatolie.lupacescu on 18/11/2015.
 */
public class KafkaRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("kafka:localhost:9092?topic=test&zookeeperHost=localhost&zookeeperPort=2181&groupId=group2&consumersCount=2")
                .to("log:input");
    }
}
