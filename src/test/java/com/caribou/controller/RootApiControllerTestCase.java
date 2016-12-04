package com.caribou.controller;

import com.caribou.IntegrationTests;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

import static org.assertj.core.api.Java6Assertions.assertThat;


@Ignore // JWT token also secures this one accidently.
public class RootApiControllerTestCase extends IntegrationTests {

    @Test
    public void routeExists() throws Exception {
        ResponseEntity<HashMap> response = get("/", HashMap.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void rootHasListOfEndpoints() throws Exception {
        ResponseEntity<HashMap> response = get("/", HashMap.class);
        assertThat(response.getBody()).isNotEmpty();
    }

}
