package com.caribou.auth.rest;


import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.jwt.UserContext;
import com.caribou.auth.rest.dto.UserAccountDto;
import com.caribou.auth.service.CompanyResourceAssembler;
import com.caribou.auth.service.UserService;
import com.caribou.company.domain.Company;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.rest.ErrorHandler;
import com.caribou.company.rest.dto.CompanyDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.core.EmbeddedWrappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import rx.Single;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Optional;


@RestController
@RequestMapping("/v1/users")
public class UserRestController {

    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyResourceAssembler companyResourceAssembler;

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public Single<UserAccountDto> me() {
        UserContext userDetails = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.findByEmail(userDetails.getUsername())
                .map(u -> modelMapper.map(u, UserAccountDto.class))
                .map(u -> {
                    Optional<Company> company = companyRepository.findByEmployeeEmail(u.getEmail());
                    if (company.isPresent()) {
                        final EmbeddedWrappers wrapper = new EmbeddedWrappers(false); // DO NOT prefer collections
                        final CompanyDto resource = toResource(company.get());
                        u.setEmbeddeds(new Resources<>(Collections.singletonList(wrapper.wrap(resource, "company"))));
                    }
                    return u;
                })
                .toSingle();
    }

    private CompanyDto toResource(Company company) {
        return companyResourceAssembler.toResource(company);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Single<ResponseEntity<UserAccountDto>> create(@Valid @RequestBody UserAccountDto newUser) {
        UserAccount user = convertToEntity(newUser);
        return userService.create(user)
                .map(d -> new ResponseEntity<>(convertToEntity(d), HttpStatus.CREATED))
                .onErrorReturn(ErrorHandler::h)
                .toSingle();
    }

    private UserAccount convertToEntity(UserAccountDto newUser) {
        return modelMapper.map(newUser, UserAccount.class);
    }

    private UserAccountDto convertToEntity(UserAccount u) {
        return modelMapper.map(u, UserAccountDto.class);
    }

}
