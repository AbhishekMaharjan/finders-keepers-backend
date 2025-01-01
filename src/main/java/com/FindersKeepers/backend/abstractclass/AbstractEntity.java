package com.FindersKeepers.backend.abstractclass;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity<PK extends Serializable> {

    @Basic
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, name = "created_date")
    public Date createdDate;

    @Basic
    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_date")
    private Date modifiedDate;

    @Basic
    @CreatedBy
    @Column(updatable = false, name = "created_by")
    private Long createdBy;

    @Basic
    @LastModifiedBy
    @Column(name = "modified_by")
    private Long modifiedBy;

}
