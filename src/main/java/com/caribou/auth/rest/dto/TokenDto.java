package com.caribou.auth.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


public class TokenDto {

    @JsonProperty
    private String token;

    public TokenDto() {
    }

    public TokenDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

}
