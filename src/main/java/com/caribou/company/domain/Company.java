package com.caribou.company.domain;

import javax.persistence.Column;
import javax.persistence.Entity;


@Entity
public class Company extends AbstractEntity {

    @Column(nullable = false)
    private String name;

    private Integer defaultDaysOf;

    public Company() {
    }

    public Company(String name, Integer defaultDaysOf) {
        this.name = name;
        this.defaultDaysOf = defaultDaysOf;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDefaultDaysOf(Integer defaultDaysOf) {
        this.defaultDaysOf = defaultDaysOf;
    }

}
