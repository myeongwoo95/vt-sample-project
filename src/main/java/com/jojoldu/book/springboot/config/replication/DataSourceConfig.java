package com.jojoldu.book.springboot.config.replication;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;

import static com.jojoldu.book.springboot.config.replication.ReplicationRoutingDataSource.DATASOURCE_KEY_MASTER;
import static com.jojoldu.book.springboot.config.replication.ReplicationRoutingDataSource.DATASOURCE_KEY_SLAVE;

@Configuration(proxyBeanMethods = false) // BeanLiteMode로 CGLIB을 사용하지 않아 프록시 모드가 안 된다.
public class DataSourceConfig {

    @Bean
    @FlywayDataSource
    @ConfigurationProperties(prefix = "spring.datasource.master") // properties 설정내용을 읽어온다.
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.slave") // properties 설정내용을 읽어온다.
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean // @Qualifier는 같은 DataSource 타입에서 특정해줄 수 있다.
    public DataSource routingDataSource(@Qualifier("masterDataSource") DataSource masterDataSource,
                                        @Qualifier("slaveDataSource") DataSource slaveDataSource) {
        ReplicationRoutingDataSource routingDataSource = new ReplicationRoutingDataSource();

        HashMap<Object, Object> sources = new HashMap<>();
        sources.put(DATASOURCE_KEY_MASTER, masterDataSource);
        sources.put(DATASOURCE_KEY_SLAVE, slaveDataSource);

        routingDataSource.setTargetDataSources(sources);
        routingDataSource.setDefaultTargetDataSource(masterDataSource);

        return routingDataSource;
    }

    @Bean
    @Primary // 이 어노테이션으로 1개의 DataSource 처럼 동작가능한 듯 하다.
    public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }
}
