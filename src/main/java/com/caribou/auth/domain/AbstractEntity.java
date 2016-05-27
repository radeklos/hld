package com.caribou.auth.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@MappedSuperclass
abstract class AbstractEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = 36)
    private Long uid;

    private Date createdAt;

    private Date updatedAt;

    public Long getUid() {
        return uid;
    }

    @PrePersist
    void createdAt() {
        this.createdAt = this.updatedAt = new Date();
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = new Date();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractEntity that = (AbstractEntity) o;

        return !(getUid() != null ? !getUid().equals(that.getUid()) : that.getUid() != null);

    }

    @Override
    public final int hashCode() {
        return getUid() != null ? getUid().hashCode() : 0;
    }
}
