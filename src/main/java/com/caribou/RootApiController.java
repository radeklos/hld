package com.caribou;


import com.caribou.auth.rest.UserRestController;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


@RestController
@RequestMapping("/")
public class RootApiController {

    @RequestMapping(method = RequestMethod.GET)
    public RootResponses nodes() {
        RootResponses data = new RootResponses();
        data.add(linkTo(methodOn(UserRestController.class).me()).withRel("userMe"));
        return data;
    }

    class RootResponses extends ResourceSupport {
    }

}
