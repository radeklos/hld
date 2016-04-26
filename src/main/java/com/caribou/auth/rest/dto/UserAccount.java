package com.caribou.auth.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class UserAccount {

    @NotEmpty
    @Size(max = 255)
    @JsonProperty
    private String firstName;

    @NotEmpty
    @Size(max = 255)
    @JsonProperty
    private String lastName;

    @NotNull
    @Size(min = 6, max = 255)
    @JsonProperty
    private String password;

    @NotEmpty
    @Email
    @Size(max = 255)
    @JsonProperty
    private String email;

    public UserAccount() {
    }

    private UserAccount(Builder builder) {
        firstName = builder.firstName;
        lastName = builder.lastName;
        password = builder.password;
        email = builder.email;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private String firstName;
        private String lastName;
        private String password;
        private String email;

        private Builder() {
        }

        public Builder firstName(String val) {
            firstName = val;
            return this;
        }

        public Builder lastName(String val) {
            lastName = val;
            return this;
        }

        public Builder password(String val) {
            password = val;
            return this;
        }

        public Builder email(String val) {
            email = val;
            return this;
        }

        public UserAccount build() {
            return new UserAccount(this);
        }
    }

}
