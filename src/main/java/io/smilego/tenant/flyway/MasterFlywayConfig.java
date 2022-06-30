package io.smilego.tenant.flyway;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "multitenancy.master.flyway.enabled", havingValue = "true", matchIfMissing = true)
public class MasterFlywayConfig {

    @Bean
    MasterFlywayMigrationInitializer masterFlywayMigrationInitializer(){
        return new MasterFlywayMigrationInitializer();
    }

}
