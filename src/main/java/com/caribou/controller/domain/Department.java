package com.caribou.controller.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;


@Entity
public class Department extends AbstractEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer daysOf;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private Company company;

}
