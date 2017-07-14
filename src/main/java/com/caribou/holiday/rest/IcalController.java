package com.caribou.holiday.rest;

import com.caribou.auth.service.UserService;
import com.caribou.company.service.CompanyService;
import com.caribou.holiday.service.LeaveService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/ical/")
public class IcalController {

    @Autowired
    private MapperFacade modelMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private LeaveService leaveService;

    @RequestMapping(method = RequestMethod.GET)
    public String getIcal(@PathVariable("userUid") String userUid) {
        return "fasd";
    }

}
