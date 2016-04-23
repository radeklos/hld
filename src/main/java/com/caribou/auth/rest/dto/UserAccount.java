package com.caribou.auth.rest.dto;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;


public class UserAccount {

    @NotNull
    @NotEmpty
    private String firstName;

    @NotNull
    @NotEmpty
    private String lastName;

    @NotNull
    @NotEmpty
    private String password;
    private String matchingPassword;

    @NotNull
    @NotEmpty
    private String email;

    public UserAccount() {
    }
}
