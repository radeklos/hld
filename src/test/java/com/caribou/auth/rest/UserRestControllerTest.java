package com.caribou.auth.rest;

import com.caribou.WebApplication;
import com.caribou.auth.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {WebApplication.class})
@WebAppConfiguration
public class UserRestControllerTest {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    UserRepository userRepository;

    private MockMvc mockMvc;
    private StatusResultMatchers status;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        status = status();

        userRepository.deleteAll();
    }

    @Test
    public void repositoryHasToHaveName() throws Exception {
        mockMvc.perform(
                put(String.format("/v1/users"))
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status.isUnprocessableEntity())
                .andExpect(jsonPath("$.name", is("Name cannot be empty")));
    }

}
