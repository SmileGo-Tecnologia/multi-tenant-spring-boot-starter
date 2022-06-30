package io.smilego.tenant.flyway;

import io.smilego.tenant.model.Tenant;
import io.smilego.tenant.persistence.TenantRepository;
import io.smilego.tenant.util.AESUtils;
import io.smilego.tenant.util.LogBuilder;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;

public class TenantFlywayMigrationInitializer implements InitializingBean {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${multitenancy.security.encryption-key}")
    private String encryptionKey;

    @Autowired
    private TenantRepository tenantRepository;

    public void migrateAllTenants(Collection<Tenant> tenants) {

        for(Tenant t : tenants){
            try (Connection connection = DriverManager.getConnection(t.getUrl(), t.getDb(), AESUtils.decrypt(encryptionKey, t.getPassword()))){

                String scriptLocation = "classpath:db/migration/tenant";
                DataSource tenantDataSource = new SingleConnectionDataSource(connection, false);

                Flyway flyway = Flyway.configure()
                        .locations(scriptLocation)
                        .baselineOnMigrate(Boolean.TRUE)
                        .dataSource(tenantDataSource)
                        .schemas("public")
                        .load();

                flyway.migrate();

            }catch (SQLException e ){
                log.error("Failed to run Flyway migrations for tenant " + t.getTenantId(), e);
            }
        }
        }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info(LogBuilder.of()
                .header("Starting flyway migrations")
                .row("Database: tenantDataSource").build());
        this.migrateAllTenants(tenantRepository.findAll());
    }

}
