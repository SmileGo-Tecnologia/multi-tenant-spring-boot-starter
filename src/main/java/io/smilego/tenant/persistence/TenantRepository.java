package io.smilego.tenant.persistence;
import io.smilego.tenant.model.Tenant;

import java.util.Optional;

public interface TenantRepository extends BaseRepository<Tenant, Long> {

    Optional<Tenant> findTenantByTenantId(String id);
}
