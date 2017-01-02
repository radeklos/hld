package com.caribou;

import com.caribou.auth.jwt.ajax.LoginRequest;
import com.caribou.auth.jwt.response.TokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {WebApplication.class})
public abstract class IntegrationTests {

    @Autowired
    ObjectMapper objectMapper;

    @Value("${local.server.port}")
    private int port = 0;

    protected <T> ResponseEntity<T> get(String path, Class<T> responseType, String username, String password) throws JsonProcessingException {
        return exchange(path, HttpMethod.GET, null, responseType, username, password);
    }

    protected <T> ResponseEntity<T> exchange(String path, HttpMethod method, Object requestBody, Class<T> responseType, String username, String password) throws JsonProcessingException {
        return testRestTemplate().exchange(
                path(path),
                method,
                new HttpEntity<>(objectMapper.writeValueAsString(requestBody), getTokenHeader(username, password)),
                responseType
        );
    }

    protected TestRestTemplate testRestTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(requestFactory);
        return new TestRestTemplate(restTemplate);
    }

    protected String path(String context) {
        return String.format("http://localhost:%s%s", port, context);
    }

    protected HttpHeaders getTokenHeader(String username, String password) throws JsonProcessingException {
        if (username == null && password == null) {
            return jsonHeader();
        }
        HttpHeaders headers = jsonHeader();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Requested-With", "XMLHttpRequest");

        LoginRequest loginRequest = new LoginRequest(username, password);
        ResponseEntity<TokenResponse> response = testRestTemplate().exchange(
                path("/v1/auth/login"),
                HttpMethod.POST,
                new HttpEntity<>(objectMapper.writeValueAsString(loginRequest), headers),
                TokenResponse.class
        );
        return getTokenHeader(response.getBody().getToken());
    }

    protected HttpHeaders jsonHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    protected HttpHeaders getTokenHeader(String token) {
        HttpHeaders headers = jsonHeader();
        headers.set("X-Authorization", String.format("Bearer %s", token));
        return headers;
    }

    protected <T> ResponseEntity<T> get(String path, Class<T> responseType) throws JsonProcessingException {
        return exchange(path, HttpMethod.GET, null, responseType, null, null);
    }

    protected <T> ResponseEntity<T> post(String path, Object requestBody, Class<T> responseType, String username, String password) throws JsonProcessingException {
        return exchange(path, HttpMethod.POST, requestBody, responseType, username, password);
    }

    protected <T> ResponseEntity<T> post(String path, Object requestBody, Class<T> responseType) throws JsonProcessingException {
        return exchange(path, HttpMethod.POST, requestBody, responseType, null, null);
    }

    protected <T> ResponseEntity<T> put(String path, Object requestBody, Class<T> responseType, String username, String password) throws JsonProcessingException {
        return exchange(path, HttpMethod.PUT, requestBody, responseType, username, password);
    }

    protected <T> ResponseEntity<T> put(String path, Object requestBody, Class<T> responseType) throws JsonProcessingException {
        return exchange(path, HttpMethod.PUT, requestBody, responseType, null, null);
    }

}
