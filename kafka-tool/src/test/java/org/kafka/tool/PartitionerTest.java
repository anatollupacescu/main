package org.kafka.tool;

import kafka.utils.VerifiableProperties;
import org.junit.Test;
import org.kafka.tool.bean.SimplePartitioner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by anatol on 8/2/15.
 */
public class PartitionerTest {

    @Test
    public void test() {
        SimplePartitioner partitioner = new SimplePartitioner(new VerifiableProperties());
        int assignedPartition = partitioner.partition("0".getBytes(), 2);
        assertThat(assignedPartition, equalTo(0));
        assignedPartition = partitioner.partition("1".getBytes(), 2);
        assertThat(assignedPartition, equalTo(1));
        assignedPartition = partitioner.partition("2".getBytes(), 2);
        assertThat(assignedPartition, equalTo(0));
    }
}
