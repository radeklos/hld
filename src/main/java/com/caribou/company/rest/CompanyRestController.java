package com.caribou.company.rest;


import com.caribou.company.domain.Company;
import com.caribou.company.rest.dto.CompanyDto;
import com.caribou.company.service.CompanyService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rx.Subscriber;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@RestController
@RequestMapping("/v1/companies")
public class CompanyRestController {

    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private CompanyService companyService;

    @RequestMapping(method = RequestMethod.PUT)
    public CompanyDto create(@Valid @RequestBody CompanyDto newCompany, HttpServletResponse response) {
        Company company = convertToEntity(newCompany);

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
            }
        });

        return newCompany;
    }

    @RequestMapping(value = "/{uid}", method = RequestMethod.POST)
    public CompanyDto update(@PathVariable("uid") Long uid, @Valid @RequestBody CompanyDto companyDto, HttpServletResponse response) {
        Company company = convertToEntity(companyDto);

        companyService.update(uid, company).subscribe(new Subscriber<Company>() {
            @Override
            public void onCompleted() {
                response.setStatus(HttpServletResponse.SC_OK);
            }

            @Override
            public void onError(Throwable e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

            @Override
            public void onNext(Company c) {
            }
        });

        return companyDto;
    }

    private Company convertToEntity(CompanyDto newCompany) {
        return modelMapper.map(newCompany, Company.class);
    }

}
