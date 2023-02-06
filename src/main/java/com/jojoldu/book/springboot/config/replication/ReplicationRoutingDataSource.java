package com.jojoldu.book.springboot.config.replication;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {

    public static final String DATASOURCE_KEY_MASTER = "master";
    public static final String DATASOURCE_KEY_SLAVE = "slave";

    @Override
    protected Object determineCurrentLookupKey() {
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();

        logger.info("This Transaction is readOnly=" + isReadOnly);

        if (isReadOnly) {
            System.out.println("slave");
            return DATASOURCE_KEY_SLAVE;
        }else{
            System.out.println("master");
            return DATASOURCE_KEY_MASTER;
        }

    }

}
