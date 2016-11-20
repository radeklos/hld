package com.caribou.auth.rest;

import com.caribou.Factory;
import com.caribou.Header;
import com.caribou.Json;
import com.caribou.WebApplication;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.auth.rest.dto.Error;
import com.caribou.auth.rest.dto.ErrorField;
import com.caribou.auth.rest.dto.UserAccountDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {WebApplication.class})
@WebAppConfiguration
@IntegrationTest({"server.port=0"})
public class UserRestControllerTest {

    @Autowired
    FilterChainProxy[] filterChainProxy;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    UserRepository userRepository;

    @Value("${local.server.port}")
    private int port = 0;

    private MockMvc mockMvc;

    private StatusResultMatchers status;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = webAppContextSetup(webApplicationContext).addFilters(filterChainProxy).build();
        status = status();
    }

    @Test
    public void requestWithEmptyJsonRequestReturnsUnprocessableEntity() throws Exception {
        UserAccountDto userAccount = UserAccountDto.newBuilder().build();

        mockMvc.perform(post("/v1/users")
                        .content(Json.dumps(userAccount))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status.isUnprocessableEntity())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    public void createNewUser() throws Exception {
        UserAccountDto userAccount = Factory.userAccountDto();
        String json = "{" +
                "\"firstName\":\"%s\"," +
                "\"lastName\":\"%s\"," +
                "\"email\":\"%s\"," +
                "\"password\":\"%s\"" +
                "}";
        json = String.format(json, userAccount.getFirstName(), userAccount.getLastName(), userAccount.getEmail(), userAccount.getPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = new TestRestTemplate().exchange(
                path("/v1/users"),
                HttpMethod.POST,
                new HttpEntity<>(json, headers),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        UserAccount user = userRepository.findByEmail(userAccount.getEmail());
        assertThat(user).as("User wasn't saved").isNotNull();
    }

    @Test
    public void cannotCreateUserWithSameEmailAddress() throws Exception {
        UserAccountDto userAccount = Factory.userAccountDto();

        ModelMapper modelMapper = new ModelMapper();
        userRepository.save(modelMapper.map(userAccount, UserAccount.class));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String payload = String.format("{\"email\":\"%s\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"password\":\"abcabc\"}", userAccount.getEmail());
        ResponseEntity<Error> response = new TestRestTemplate().exchange(
                path("/v1/users"),
                HttpMethod.POST,
                new HttpEntity<>(payload, headers),
                Error.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        ErrorField emailError = response.getBody().getValidationErrors().get("email");
        assertThat(emailError.getCode()).isEqualTo("must be unique");
        assertThat(emailError.getDefaultMessage()).isEqualTo("Email is already taken");
        assertThat(emailError.getRejectedValue()).isEqualTo(userAccount.getEmail());

        UserAccount user = userRepository.findByEmail(userAccount.getEmail());
        assertThat(user).as("User wasn't saved").isNotNull();
    }

    @Test
    public void userDetailIsUnauthorized() throws Exception {
        UserAccount userAccount = Factory.userAccount();
        userRepository.save(userAccount);

        mockMvc.perform(get(String.format("/v1/users/me", userAccount.getUid()))).andExpect(status.isUnauthorized());
    }

    @Test
    public void getMineUserDetail() throws Exception {
        UserAccount userAccount = Factory.userAccount();
        userRepository.save(userAccount);

        mockMvc.perform(
                get(String.format("/v1/users/me", userAccount.getUid()))
                        .headers(Header.basic(userAccount.getEmail(), userAccount.getPassword())
                        )
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.firstName").value(userAccount.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(userAccount.getLastName()))
                .andExpect(jsonPath("$.email").value(userAccount.getEmail()))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    private String path(String context) {
        return String.format("http://localhost:%s%s", port, context);
    }

}
