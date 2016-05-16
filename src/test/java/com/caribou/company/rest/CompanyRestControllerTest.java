package com.caribou.company.rest;

import com.caribou.Header;
import com.caribou.Json;
import com.caribou.WebApplication;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.company.domain.Company;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.rest.dto.CompanyDto;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletResponse;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {WebApplication.class})
@WebAppConfiguration
public class CompanyRestControllerTest {

    private static UserAccount userAccount;

    private static HttpHeaders authHeader;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected FilterChainProxy[] filterChainProxy;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    UserRepository userRepository;

    private MockMvc mockMvc;

    private StatusResultMatchers status;

    @BeforeClass
    public static void before() {
        userAccount = UserAccount.newBuilder()
                .email("john.doe@email.com")
                .firstName("John")
                .lastName("Doe")
                .password("abcabc")
                .build();
        authHeader = Header.basic("john.doe@email.com", "abcabc");
    }

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = webAppContextSetup(webApplicationContext).addFilters(filterChainProxy).build();
        status = status();

        companyRepository.deleteAll();
        userRepository.deleteAll();

        userRepository.save(userAccount);
    }

    @Test
    public void requestWithEmptyJsonRequestReturnsUnprocessableEntity() throws Exception {
        CompanyDto company = CompanyDto.newBuilder().build();

        mockMvc.perform(
                put("/v1/companies")
                        .content(Json.dumps(company))
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(authHeader))
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
                        .headers(authHeader)
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
                        .headers(authHeader)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(result.getResponse().getContentAsString(), HttpServletResponse.SC_OK, result.getResponse().getStatus());
    }

    @Test
    public void updateNonExistingCompany() throws Exception {
        CompanyDto companyDto = CompanyDto.newBuilder()
                .name("company name")
                .defaultDaysOf(10)
                .build();

        mockMvc.perform(
                post("/v1/companies/0")
                        .content(Json.dumps(companyDto))
                        .headers(authHeader)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status.isNotFound());
    }

    @Test
    public void getCompany() throws Exception {
        Company company = new Company("company name", 15);
        companyRepository.save(company);

        mockMvc.perform(
                get(String.format("/v1/companies/%s", company.getUid()))
                        .headers(authHeader))
                .andExpect(jsonPath("$.uid", is(new Integer(String.valueOf(company.getUid())))))
                .andExpect(jsonPath("$.name", is(company.getName())))
                .andExpect(jsonPath("$.defaultDaysOf", is(company.getDefaultDaysOf())));
    }

    @Test
    public void nonExisting() throws Exception {
        mockMvc.perform(get("/v1/companies/0").headers(authHeader)).andExpect(status.isNotFound());
    }

}
