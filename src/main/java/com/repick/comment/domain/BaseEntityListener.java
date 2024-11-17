package com.repick.comment.domain;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class BaseEntityListener {

    @PrePersist
    public void prePersist(BaseEntity baseEntity) {
        baseEntity.onCreate();
    }

    @PreUpdate
    public void preUpdate(BaseEntity baseEntity) {
        baseEntity.onUpdate();
    }
}
