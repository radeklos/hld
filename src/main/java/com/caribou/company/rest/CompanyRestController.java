package com.caribou.company.rest;


import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.service.UserService;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Role;
import com.caribou.company.rest.dto.CompanyDto;
import com.caribou.company.service.CompanyService;
import com.caribou.company.service.NotFound;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import rx.Observable;
import rx.Subscriber;

import javax.servlet.http.HttpServletResponse;
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
        Observable<CompanyDto> bla = companyService.get(uid).map(u -> modelMapper.map(u, CompanyDto.class));
        return bla.toBlocking().first();
    }

    @RequestMapping(method = RequestMethod.PUT)
    public CompanyDto create(@Valid @RequestBody CompanyDto newCompany, HttpServletResponse response) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserAccount loggedUser = userService.findByEmail(userDetails.getUsername()).toBlocking().first();

        Company company = convert(newCompany);
        company.addEmployee(loggedUser, Role.Owner);
        companyService.create(company).subscribe(new Subscriber<Company>() {
            @Override
            public void onCompleted() {
                response.setStatus(HttpServletResponse.SC_CREATED);
            }

            @Override
            public void onError(Throwable e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

            @Override
            public void onNext(Company c) {
                modelMapper.map(c, newCompany);
            }
        });

        return newCompany;
    }

    @RequestMapping(value = "/{uid}", method = RequestMethod.POST)
    public CompanyDto update(@PathVariable("uid") Long uid, @Valid @RequestBody CompanyDto companyDto, HttpServletResponse response) {
        Company company = convert(companyDto);
        companyService.update(uid, company).subscribe(new Subscriber<Company>() {
            @Override
            public void onCompleted() {
                response.setStatus(HttpServletResponse.SC_OK);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof NotFound) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            }

            @Override
            public void onNext(Company c) {
                modelMapper.map(c, companyDto);
            }
        });

        return companyDto;
    }

    private Company convert(CompanyDto newCompany) {
        return modelMapper.map(newCompany, Company.class);
    }

}
