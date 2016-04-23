package com.caribou.controller.domain;

import javax.persistence.Column;
import javax.persistence.Entity;


@Entity
public class Company extends AbstractEntity {

    @Column(nullable = false)
    private String name;

    private Integer defaultDaysOf;

}
