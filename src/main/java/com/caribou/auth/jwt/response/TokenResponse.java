package com.caribou.auth.jwt.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class TokenResponse {

    private String token;
    private String refreshToken;

    @JsonCreator
    public TokenResponse(@JsonProperty("token") String token, @JsonProperty("refreshToken") String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

}
