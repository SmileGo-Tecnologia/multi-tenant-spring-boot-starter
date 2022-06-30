package io.smilego.tenant;

import io.smilego.tenant.persistence.hibernate.CurrentTenantIdentifierResolverImpl;
import io.smilego.tenant.persistence.hibernate.DynamicDataSourceBasedMultiTenantConnectionProvider;
import io.smilego.tenant.service.TenantService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "multitenancy.enabled", havingValue = "true", matchIfMissing = true)
public class TenantConfiguration {

    @Bean
    public TenantInterceptor tenantInterceptor(){
        return new TenantInterceptor();
    }

    @Bean
    public WebConfiguration webConfiguration(TenantInterceptor tenantInterceptor){
        return new WebConfiguration(tenantInterceptor);
    }

    @Bean
    public TenantService tenantService(){
        return new TenantService();
    }

    @Bean
    public DynamicDataSourceBasedMultiTenantConnectionProvider dynamicDataSourceBasedMultiTenantConnectionProvider(){
        return new DynamicDataSourceBasedMultiTenantConnectionProvider();
    }

    @Bean
    public CurrentTenantIdentifierResolverImpl currentTenantIdentifierResolver(){
        return new CurrentTenantIdentifierResolverImpl();
    }

}
