package com.caribou.company.rest;


import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.jwt.UserContext;
import com.caribou.auth.service.UserService;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Role;
import com.caribou.company.rest.dto.CompanyDto;
import com.caribou.company.service.CompanyService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequestMapping("/v1/companies")
public class CompanyRestController {

    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/{uid}", method = RequestMethod.GET)
    public CompanyDto get(@PathVariable("uid") Long uid) {
        UserContext userDetails = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return companyService.getForEmployeeEmail(uid, userDetails.getUsername())
                .map(c -> convert(c))
                .toBlocking()
                .first();
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<CompanyDto> create(@Valid @RequestBody CompanyDto newCompany) {
        UserContext userDetails = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserAccount loggedUser = userService.findByEmail(userDetails.getUsername()).toBlocking().first();

        Company company = convert(newCompany);
        company.addEmployee(loggedUser, Role.Owner);
        return companyService.create(company)
                .map(d -> new ResponseEntity<>(convert(d), HttpStatus.CREATED))
                .toBlocking().first();
    }

    @RequestMapping(value = "/{uid}", method = RequestMethod.PUT)
    public CompanyDto update(@PathVariable("uid") Long uid, @Valid @RequestBody CompanyDto companyDto) {
        // TODO some acl
        Company company = convert(companyDto);
        return companyService.update(uid, company)
                .map(d -> convert(d))
                .toBlocking().first();
    }

    private Company convert(CompanyDto newCompany) {
        return modelMapper.map(newCompany, Company.class);
    }

    private CompanyDto convert(Company company) {
        return modelMapper.map(company, CompanyDto.class);
    }

}
