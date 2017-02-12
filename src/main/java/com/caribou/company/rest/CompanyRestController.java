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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rx.Single;

import javax.validation.Valid;
import java.util.Arrays;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


@RestController
@RequestMapping("/v1/companies")
public class CompanyRestController {

    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/{uid}", method = RequestMethod.GET)
    public Single<CompanyDto> get(@PathVariable("uid") Long uid) {
        UserContext userDetails = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return companyService.getByEmployeeEmail(uid, userDetails.getUsername())
                .map(c -> convert(c.getCompany()))
                .toSingle();
    }

    private CompanyDto convert(Company company) {
        CompanyDto companyDto = modelMapper.map(company, CompanyDto.class);
        companyDto.add(linkTo(methodOn(DepartmentRestController.class).getList(company.getUid())).withRel("department"));
        return companyDto;
    }

    @RequestMapping(method = RequestMethod.POST)
    public Single<ResponseEntity<CompanyDto>> create(@Valid @RequestBody CompanyDto newCompany) {
        UserContext userDetails = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserAccount loggedUser = userService.findByEmail(userDetails.getUsername()).toBlocking().first();

        Company company = convert(newCompany);
        company.addEmployee(loggedUser, Role.Owner);
        return companyService.create(company)
                .map(d -> new ResponseEntity<>(convert(d), HttpStatus.CREATED))
                .toSingle();
    }

    private Company convert(CompanyDto newCompany) {
        return modelMapper.map(newCompany, Company.class);
    }

    @RequestMapping(value = "/{uid}", method = RequestMethod.PUT)
    public Single<CompanyDto> update(@PathVariable("uid") Long uid, @Valid @RequestBody CompanyDto companyDto) {
        // TODO some acl
        Company company = convert(companyDto);
        return companyService.update(uid, company)
                .map(this::convert)
                .toSingle();
    }

    @RequestMapping(value = "/{uid}/employees", method = RequestMethod.POST)
    public Single<String> update(@PathVariable("uid") Long uid, @RequestParam("file") MultipartFile file) {
        UserContext userDetails = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return companyService.getByEmployeeEmail(uid, userDetails.getUsername())
                .map(employee -> {
                    if (!Arrays.asList(Role.Admin, Role.Editor).contains(employee.getRole())) {
                        throw new AccessDeniedException("omg");
                    }
                    return employee.getMember().getEmail();
                }).toSingle();
    }

}
