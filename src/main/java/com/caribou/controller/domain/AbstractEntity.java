package com.caribou.controller.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Long getUid() {
        return uid;
    }

}
