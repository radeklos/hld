package com.caribou.auth.rest;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.jwt.ajax.LoginRequest;
import com.caribou.auth.jwt.response.TokenResponse;
import com.caribou.auth.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;


public class LoginRestControllerTest extends IntegrationTests {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void authorizedWithValidUsernameAndPassword() throws Exception {
        UserAccount user = Factory.userAccount();
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest(user.getEmail(), user.getPassword());
        objectMapper.writeValueAsString(loginRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Requested-With", "XMLHttpRequest");

        ResponseEntity<TokenResponse> response = testRestTemplate().exchange(
                path("/v1/auth/login"),
                HttpMethod.POST,
                new HttpEntity<>(objectMapper.writeValueAsString(loginRequest), headers),
                TokenResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getToken()).isNotNull();
        assertThat(response.getBody().getRefreshToken()).isNotNull();
    }

    @Test
    public void authorizedWithInvalidUsernameAndPassword() throws Exception {
        UserAccount user = Factory.userAccount();
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest(user.getEmail(), "incorrect password");
        objectMapper.writeValueAsString(loginRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Requested-With", "XMLHttpRequest");

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
