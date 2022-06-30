package io.smilego.tenant.persistence.hibernate;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.smilego.tenant.model.Tenant;
import io.smilego.tenant.service.TenantService;
import io.smilego.tenant.util.AESUtils;
import io.smilego.tenant.util.LogBuilder;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class DynamicDataSourceBasedMultiTenantConnectionProvider
        extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    private static final String TENANT_POOL_NAME_SUFFIX = "DataSource";

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("masterDataSource")
    private DataSource masterDataSource;

    @Autowired
    @Qualifier("masterDataSourceProperties")
    private DataSourceProperties dataSourceProperties;

    @Autowired
    @Qualifier("tenantHikariConfig")
    private HikariConfig hikariConfig;

    @Value("${multitenancy.datasource-cache.maximumSize:100}")
    private Long maximumSize;

    @Value("${multitenancy.datasource-cache.expireAfterAccess:10}")
    private Integer expireAfterAccess;

    @Value("${multitenancy.security.encryption-key}")
    private String encryptionKey;

    @Autowired
    private TenantService tenantService;

    private LoadingCache<String, DataSource> tenantDataSources;

    @PostConstruct
    private void createCache() {
        tenantDataSources = CacheBuilder.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterAccess(expireAfterAccess, TimeUnit.MINUTES)
                .removalListener((RemovalListener<String, DataSource>) removal -> {
                    HikariDataSource ds = (HikariDataSource) removal.getValue();
                    ds.close(); // tear down properly
                    log.info(LogBuilder.of()
                            .header("Closing datasource")
                            .row("Pool name: ", ds.getPoolName()).build());
                })
                .build(new CacheLoader<String, DataSource>() {
                    public DataSource load(String key) {
                        Tenant tenant = tenantService.getTenant(key);
                        return createAndConfigureDataSource(tenant);
                    }
                });
    }

    private DataSource createAndConfigureDataSource(Tenant tenant) {

        HikariConfig config = this.hikariConfig;

        config.setUsername(tenant.getDb());
        config.setPassword(AESUtils.decrypt(encryptionKey, tenant.getPassword()));
        config.setJdbcUrl(tenant.getUrl());
        config.setPoolName(tenant.getTenantId() + TENANT_POOL_NAME_SUFFIX);

        HikariDataSource ds = new HikariDataSource(config);

        log.info(LogBuilder.of()
                .header("Configured datasource")
                .row("Pool name: ", ds.getPoolName()).build());
        return ds;
    }
    @Override
    protected DataSource selectAnyDataSource() {
        return masterDataSource;
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {

        try {
            return tenantDataSources.get(tenantIdentifier);
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to load DataSource for tenant: " + tenantIdentifier);
        }
    }
}
