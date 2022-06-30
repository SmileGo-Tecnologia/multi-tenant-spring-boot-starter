package io.smilego.tenant.flyway;

import io.smilego.tenant.util.LogBuilder;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

public class MasterFlywayMigrationInitializer implements InitializingBean {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier(value = "masterDataSource")
    private DataSource masterDataSource;

    public void migrate() {
        String scriptLocation = "classpath:db/migration/master";

            Flyway flyway = Flyway.configure()
                    .locations(scriptLocation)
                    .baselineOnMigrate(Boolean.TRUE)
                    .dataSource(masterDataSource)
                    .schemas("public")
                    .load();

            flyway.migrate();
        }

    @Override
    @DependsOn(value = "masterDataSource")
    public void afterPropertiesSet() throws Exception {
        log.info(LogBuilder.of()
                        .header("Starting flyway migrations")
                        .row("Database: masterDataSource").build());
        this.migrate();
    }
}
