package io.smilego.tenant.flyway;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@ConditionalOnProperty(name = "multitenancy.tenant.flyway.enabled", havingValue = "true", matchIfMissing = true)
public class TenantFlywayConfig {

    @Bean
    @DependsOn(value = "masterFlywayMigrationInitializer")
    TenantFlywayMigrationInitializer tenantFlywayMigrationInitializer(){
        return new TenantFlywayMigrationInitializer();
    }

}
