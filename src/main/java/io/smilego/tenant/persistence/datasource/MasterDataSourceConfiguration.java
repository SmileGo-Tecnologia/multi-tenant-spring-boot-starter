package io.smilego.tenant.persistence.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Configuration
@ConditionalOnMissingBean(DataSource.class)
public class MasterDataSourceConfiguration {

    @Bean
    @ConfigurationProperties("multitenancy.master.datasource")
    @Primary
    public DataSourceProperties masterDataSourceProperties(){
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("multitenancy.master.datasource.hikari")
    @Primary
    public DataSource masterDataSource(){
        HikariDataSource dataSource = masterDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();

        dataSource.setPoolName("masterDataSource");

        return dataSource;
    }

    @Bean
    @ConfigurationProperties("multitenancy.tenant.datasource.hikari")
    public HikariConfig tenantHikariConfig(){
        return new HikariConfig();
    }

}
