package com.caribou;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;


@RestController
@RequestMapping("/")
public class RootApiController {

    @RequestMapping(method = RequestMethod.GET)
    public ArrayList<String> nodes() {
        ArrayList<String> data = new ArrayList<>();
        data.add("/v1/users/");
        return data;
    }

}
