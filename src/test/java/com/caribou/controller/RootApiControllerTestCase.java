package com.caribou.controller;

import com.caribou.WebApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebApplication.class)
@WebAppConfiguration
public class RootApiControllerTestCase {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private StatusResultMatchers status;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        status = status();
    }

    @Test
    public void routeExists() throws Exception {
        mockMvc.perform(get("/")).andExpect(status.isOk());
    }

    @Test
    public void rootHasListOfEndpoints() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status.isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

}
