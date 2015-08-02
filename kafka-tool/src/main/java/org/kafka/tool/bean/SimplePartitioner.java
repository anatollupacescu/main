package org.kafka.tool.bean;

import com.google.common.base.Strings;
import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by anatol on 8/2/15.
 */
public class SimplePartitioner implements Partitioner {

    private static final Logger logger = LoggerFactory.getLogger(SimplePartitioner.class);

    public SimplePartitioner (VerifiableProperties props) {
    }

    @Override
    public int partition(Object key, int numPartitions) {
        String keyString = new String((byte[])key);
        Integer partition = Integer.valueOf(keyString);
        if(partition < numPartitions) {
            return partition;
        }
        logger.debug("Incorrect partition number ({} of {}), sending to partition 0", partition, numPartitions);
        return 0;
    }
}
