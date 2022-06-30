package io.smilego.tenant.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PrePersist;

public class BaseEntityListener {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @PrePersist
    public void baseEntityPrePersist(BaseEntity<?> baseEntity) {
        baseEntity.definirData();
    }

}
