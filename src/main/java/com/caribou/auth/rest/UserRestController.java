package com.caribou.auth.rest;


import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.jwt.UserContext;
import com.caribou.auth.rest.dto.UserAccountDto;
import com.caribou.auth.service.UserService;
import com.caribou.company.domain.Company;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.rest.CompanyRestController;
import com.caribou.company.rest.ErrorHandler;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyRepository companyRepository;

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public Single<UserAccountDto> me() {
        UserContext userDetails = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.findByEmail(userDetails.getUsername())
                .map(u -> modelMapper.map(u, UserAccountDto.class))
                .map(u -> {
                    Optional<Company> company = companyRepository.findByEmployeeEmail(u.getEmail());
                    company.ifPresent(company1 -> u.add(linkTo(methodOn(CompanyRestController.class).get(company1.getUid())).withRel("company")));
                    return u;
                })
                .toSingle();
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
