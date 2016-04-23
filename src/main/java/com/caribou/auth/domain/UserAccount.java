package com.caribou.auth.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;


@Entity
@Table(indexes = {@Index(columnList = "email, password")})
public class UserAccount extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column
    private String firstName;
    @Column
    private String lastName;

    public UserAccount() {
    }

    public UserAccount(String email, String password) {
        this.email = email;
        this.password = password;
    }
//
//    @Column
//    private Date started;
//
//    @ManyToOne(cascade = CascadeType.ALL, optional = false)
//    private Company company;
//
//    @ManyToOne(cascade = CascadeType.ALL, optional = false)
//    private Department department;

}
