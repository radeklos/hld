package com.caribou.company.rest;

import com.caribou.Json;
import com.caribou.WebApplication;
import com.caribou.company.domain.Company;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.rest.dto.CompanyDto;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletResponse;

import static junit.framework.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {WebApplication.class})
@WebAppConfiguration
public class CompanyRestControllerTest {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    CompanyRepository companyRepository;

    private MockMvc mockMvc;
    private StatusResultMatchers status;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        status = status();

        companyRepository.deleteAll();
    }

    @Test
    public void requestWithEmptyJsonRequestReturnsUnprocessableEntity() throws Exception {
        CompanyDto company = CompanyDto.newBuilder().build();

        mockMvc.perform(
                put("/v1/companies")
                        .content(Json.dumps(company))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status.isUnprocessableEntity());
    }

    @Test
    public void createNewCompany() throws Exception {
        CompanyDto company = CompanyDto.newBuilder()
                .name("company name")
                .defaultDaysOf(10)
                .build();

        MvcResult result = mockMvc.perform(
                put("/v1/companies")
                        .content(Json.dumps(company))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(result.getResponse().getContentAsString(), HttpServletResponse.SC_CREATED, result.getResponse().getStatus());
    }

    @Test
    public void updateCompany() throws Exception {
        Company company = new Company("company name", 15);
        companyRepository.save(company);

        CompanyDto companyDto = CompanyDto.newBuilder()
                .name("company name")
                .defaultDaysOf(10)
                .build();

        MvcResult result = mockMvc.perform(
                post(String.format("/v1/companies/%s", company.getUid()))
                        .content(Json.dumps(companyDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(result.getResponse().getContentAsString(), HttpServletResponse.SC_OK, result.getResponse().getStatus());
    }

}
