package com.caribou.auth.rest;

import com.caribou.Factory;
import com.caribou.WebApplication;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.auth.rest.dto.TokenDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {WebApplication.class})
@WebAppConfiguration
@IntegrationTest({"server.port=0"})
public class LoginRestControllerTest {

    @Autowired
    UserRepository userRepository;

    @Value("${local.server.port}")
    private int port = 0;

    @Test
    public void loginViewIsUnauthorized() throws Exception {
        ResponseEntity<TokenDto> response = new TestRestTemplate().getForEntity(path("/v1/login"), TokenDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void authorizedWithValidUsernameAndPassword() throws Exception {
        UserAccount user = Factory.userAccount();
        userRepository.save(user);

        TestRestTemplate rest = new TestRestTemplate(user.getEmail(), user.getPassword());
        ResponseEntity<TokenDto> response = rest.getForEntity(path("/v1/login"), TokenDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(response.getHeaders().get("x-auth-token")).isNotNull();
        assertThat(response.getBody().getToken()).isEqualTo(response.getHeaders().get("x-auth-token").get(0));
    }

    @Test
    public void authorizedWithInvalidUsernameAndPassword() throws Exception {
        UserAccount user = Factory.userAccount();
        userRepository.save(user);

        TestRestTemplate rest = new TestRestTemplate(user.getEmail(), "incorrectpassword");
        ResponseEntity<TokenDto> response = rest.getForEntity(path("/v1/login"), TokenDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        assertThat(response.getHeaders().get("x-auth-token")).isNull();
    }

    private String path(String context) {
        return String.format("http://localhost:%s%s", port, context);
    }

}
