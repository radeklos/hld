package com.caribou.auth.rest;


import com.caribou.auth.rest.dto.UserAccountDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/login")
public class LoginRestController {

    @RequestMapping(method = RequestMethod.GET)
    public UserAccountDto get() {
        return null;
    }

}
