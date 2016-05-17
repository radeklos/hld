package com.caribou.auth.rest;

import com.caribou.Header;
import com.caribou.Json;
import com.caribou.WebApplication;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.auth.rest.dto.UserAccountDto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletResponse;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {WebApplication.class})
@WebAppConfiguration
public class UserRestControllerTest {

    @Autowired
    protected FilterChainProxy[] filterChainProxy;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    UserRepository userRepository;

    private MockMvc mockMvc;
    private StatusResultMatchers status;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = webAppContextSetup(webApplicationContext).addFilters(filterChainProxy).build();
        status = status();
    }

    @After
    public void tearDown() throws Exception {
        userRepository.deleteAll();
    }

    @Test
    public void requestWithEmptyJsonRequestReturnsUnprocessableEntity() throws Exception {
        UserAccountDto userAccount = UserAccountDto.newBuilder().build();

        mockMvc.perform(
                put("/v1/users")
                        .content(Json.dumps(userAccount))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status.isUnprocessableEntity());
    }

    @Test
    public void createNewUser() throws Exception {
        MvcResult result = mockMvc.perform(
                put("/v1/users")
                        .content("{\"email\":\"john.doe@email.com\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"password\":\"abcabc\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(result.getResponse().getContentAsString(), HttpServletResponse.SC_CREATED, result.getResponse().getStatus());

        UserAccount user = userRepository.findByEmail("john.doe@email.com");
        assertNotNull("User wasn't saved", user);
    }

    @Test
    public void cannotCreateUserWithSameEmailAddress() throws Exception {
        UserAccountDto userAccount = UserAccountDto.newBuilder()
                .email("john.doe@email.com")
                .firstName("John")
                .lastName("Doe")
                .password("abcabc")
                .build();

        ModelMapper modelMapper = new ModelMapper();
        userRepository.save(modelMapper.map(userAccount, UserAccount.class));

        MvcResult result = mockMvc.perform(
                put("/v1/users")
                        .content("{\"email\":\"john.doe@email.com\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"password\":\"abcabc\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(result.getResponse().getContentAsString(), HttpServletResponse.SC_CONFLICT, result.getResponse().getStatus());

        UserAccount user = userRepository.findByEmail("john.doe@email.com");
        assertNotNull("User wasn't saved", user);
    }

    @Test
    public void userDetailIsUnauthorized() throws Exception {
        UserAccount userAccount = UserAccount.newBuilder()
                .email("john.doe@email.com")
                .firstName("John")
                .lastName("Doe")
                .password("abcabc")
                .build();
        userRepository.save(userAccount);

        mockMvc.perform(get(String.format("/v1/users/%s", userAccount.getUid()))).andExpect(status.isUnauthorized());
    }

    @Test
    public void loginWithIncorrectCredentials() throws Exception {
        mockMvc.perform(post("/v1/users")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "john.doe@email.com")
                .param("password", "abcabc")
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void loginWithCorrectCredentials() throws Exception {
        UserAccount userAccount = UserAccount.newBuilder()
                .email("john.doe@email.com")
                .firstName("John")
                .lastName("Doe")
                .password("abcabc")
                .build();
        userRepository.save(userAccount);

        MvcResult result = mockMvc.perform(get(String.format("/v1/users/%s", userAccount.getUid()))
                .headers(Header.basic("john.doe@email.com", "abcabc")))
                .andReturn();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        // assertNotNull(result.getResponse().getHeader("x-auth-token"));
    }

    @Test
    public void getUserDetail() throws Exception {
        UserAccount userAccount = UserAccount.newBuilder()
                .email("john.doe@email.com")
                .firstName("John")
                .lastName("Doe")
                .password("abcabc")
                .build();
        userRepository.save(userAccount);

        mockMvc.perform(
                get(String.format("/v1/users/%s", userAccount.getUid())).headers(Header.basic("john.doe@email.com", "abcabc"))
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@email.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

}
