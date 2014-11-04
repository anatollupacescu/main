package com.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.config.CFMetaData;
import org.apache.cassandra.config.KSMetaData;
import org.apache.cassandra.config.Schema;
import org.apache.cassandra.db.ColumnFamilyType;
import org.apache.cassandra.db.marshal.UTF8Type;
import org.apache.cassandra.locator.AbstractReplicationStrategy;
import org.apache.cassandra.locator.SimpleStrategy;
import org.apache.cassandra.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum EmbeddedCassandra {
	INSTANCE;

	private final Logger logger = LoggerFactory.getLogger(EmbeddedCassandra.class);
	
	private EmbeddedCassandra() {
		try {
			StorageService.instance.initServer();
			logger.debug("sleeping");
            Thread.sleep(10000L);
            logger.debug("woke up!");
			loadDataSchema("myKs", Arrays.asList("users"));
		} catch (Throwable t) {
			logger.debug("Received error when bootstrapping data schema, most likely it exists already." + t.getMessage());
		}
	}

    private void loadDataSchema(String keyspaceName, List<String> colFamilyNames) {
        Collection<KSMetaData> schema = new ArrayList<KSMetaData>();
        Class<? extends AbstractReplicationStrategy> strategyClass = SimpleStrategy.class;
        Map<String, String> strategyOptions = KSMetaData.optsWithRF(1);

        CFMetaData[] cfDefs = new CFMetaData[colFamilyNames.size()];
        for (int i = 0; i < colFamilyNames.size(); i++) {
            CFMetaData cfDef = new CFMetaData(keyspaceName, colFamilyNames.get(i), ColumnFamilyType.Standard,
                    UTF8Type.instance, null);
            cfDefs[i] = cfDef;
        }

        KSMetaData validKsMetadata = KSMetaData.testMetadata(keyspaceName, strategyClass, strategyOptions, cfDefs);
        schema.add(validKsMetadata);

        Schema.instance.load(schema);
        logger.debug("======================= LOADED DATA SCHEMA FOR TESTS ==========================");
    }
}
