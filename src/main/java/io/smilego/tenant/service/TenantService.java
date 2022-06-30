package io.smilego.tenant.service;

import io.smilego.tenant.model.Tenant;
import io.smilego.tenant.persistence.TenantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.cache.annotation.Cacheable;
import java.util.List;

public class TenantService {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String TENANT_POOL_NAME_SUFFIX = "DataSource";

    @Autowired
    @Qualifier("masterDataSourceProperties")
    private DataSourceProperties dataSourceProperties;

    @Autowired
    private TenantRepository tenantRepository;

    public Tenant getTenant(String tenantIdentifier){

        return this.tenantRepository.findTenantByTenantId(tenantIdentifier)
                .orElseThrow(() -> new RuntimeException("Erro ao criar o tenant" + tenantIdentifier));
    }

    public List<Tenant> getAllTenants(){
        return tenantRepository.findAll();
    }

    public Tenant saveTenant(Tenant tenant){
        return this.tenantRepository.save(tenant);
    }

}
