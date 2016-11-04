package com.caribou.auth.rest;


import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.rest.dto.UserAccountDto;
import com.caribou.auth.service.UserService;
import com.caribou.company.rest.ErrorHandler;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import rx.Observable;
import rx.Single;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@RestController
@RequestMapping("/v1/users")
public class UserRestController {

    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public UserAccountDto login() {
        return null; // TODO return current user details
    }

    @RequestMapping(value = "/{uid}", method = RequestMethod.GET)
    public UserAccountDto get(@PathVariable("uid") Long uid) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Observable<UserAccountDto> bla = userService.findByEmail(userDetails.getUsername())
                .map(u -> modelMapper.map(u, UserAccountDto.class));
        return bla.toBlocking().first();
    }

    @RequestMapping(method = RequestMethod.POST)
    public Single<ResponseEntity<UserAccountDto>> create(@Valid @RequestBody UserAccountDto newUser, HttpServletResponse response) {
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
