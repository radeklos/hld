package com.caribou.auth.rest;


import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.jwt.UserContext;
import com.caribou.auth.rest.dto.NestedSingleObject;
import com.caribou.auth.rest.dto.UserAccountDto;
import com.caribou.auth.service.UserService;
import com.caribou.company.domain.Company;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.rest.CompanyRestController;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import rx.Single;

import javax.validation.Valid;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


@RestController
@RequestMapping("/v1/users")
public class UserRestController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyRepository companyRepository;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserAccountDto> create(@Validated(UserAccountDto.CreateGroup.class) @RequestBody UserAccountDto newUser) {
        UserAccount user = userService.register(convertToEntity(newUser));
        return new ResponseEntity<>(convertToEntity(user), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public Single<UserAccountDto> me() {
        UserContext userDetails = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.findByEmail(userDetails.getUsername())
                .map(this::convertToEntity)
                .map(this::addNested)
                .toSingle();
    }

    @RequestMapping(path = "/me", method = RequestMethod.PUT)
    public ResponseEntity<UserAccountDto> update(@Valid @RequestBody UserAccountDto newUser) {
        UserContext userDetails = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserAccount user = userService.update(userDetails.getUid(), convertToEntity(newUser));
        return new ResponseEntity<>(convertToEntity(user), HttpStatus.OK);
    }

    private UserAccount convertToEntity(UserAccountDto newUser) {
        return modelMapper.map(newUser, UserAccount.class);
    }

    private UserAccountDto convertToEntity(UserAccount u) {
        return modelMapper.map(u, UserAccountDto.class);
    }

    private UserAccountDto addNested(UserAccountDto userAccountDto) {
        Optional<Company> company = companyRepository.findByEmployeeEmail(userAccountDto.getEmail());
        company.ifPresent(c -> userAccountDto.setCompany(nested(c)));
        return userAccountDto;
    }

    private NestedSingleObject nested(Company company) {
        return NestedSingleObject.builder()
                .href(linkTo(methodOn(CompanyRestController.class).get(company.getUid().toString())).toString())
                .uri("chll:company:" + company.getUid())
                .uid(company.getUid().toString())
                .build();
    }

}
