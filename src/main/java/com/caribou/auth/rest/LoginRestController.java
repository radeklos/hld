package com.caribou.auth.rest;


import com.caribou.auth.repository.UserRepository;
import com.caribou.auth.rest.dto.TokenDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

@RestController
@RequestMapping("/v1/login")
public class LoginRestController {

    @Autowired
    UserRepository userRepository;

    @RequestMapping(method = RequestMethod.GET)
    public TokenDto get() {
        String token = RequestContextHolder.currentRequestAttributes().getSessionId();
        return new TokenDto(token);
    }

}
