package com.caribou.auth.rest;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.jwt.ajax.LoginRequest;
import com.caribou.auth.jwt.response.TokenResponse;
import com.caribou.auth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import rx.observers.TestSubscriber;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;


public class LoginRestControllerTest extends IntegrationTests {

    @Autowired
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void authorizedWithValidUsernameAndPassword() throws Exception {
        UserAccount user = Factory.userAccount();
        String userPassword = user.getPassword();
        userService.create(user).subscribe(new TestSubscriber<>());

        LoginRequest loginRequest = new LoginRequest(user.getEmail(), userPassword);
        objectMapper.writeValueAsString(loginRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Origin", path(""));

        ResponseEntity<TokenResponse> response = testRestTemplate().exchange(
                path("/v1/auth/login"),
                HttpMethod.POST,
                new HttpEntity<>(objectMapper.writeValueAsString(loginRequest), headers),
                TokenResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getToken()).isNotNull();
    }

    @Test
    public void authorizedWithInvalidUsernameAndPassword() throws Exception {
        UserAccount user = Factory.userAccount();
        userService.create(user).subscribe(new TestSubscriber<>());

        LoginRequest loginRequest = new LoginRequest(user.getEmail(), "incorrect password");
        objectMapper.writeValueAsString(loginRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<HashMap> response = testRestTemplate().exchange(
                path("/v1/auth/login"),
                HttpMethod.POST,
                new HttpEntity<>(objectMapper.writeValueAsString(loginRequest), headers),
                HashMap.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().get("message")).isEqualTo("Invalid username or password");
    }

    @Test
    public void loginViewOptionsIsAllowed() throws Exception {
        ResponseEntity<HashMap> response = testRestTemplate().exchange(
                path("/v1/auth/login"),
                HttpMethod.OPTIONS,
                new HttpEntity<>(objectMapper.writeValueAsString(null), new HttpHeaders()),
                HashMap.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
    }

}
