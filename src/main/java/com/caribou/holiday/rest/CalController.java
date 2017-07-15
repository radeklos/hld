package com.caribou.holiday.rest;

import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.company.service.CompanyService;
import com.caribou.company.service.NotFound;
import com.caribou.holiday.service.ICalService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@RequestMapping("/cal")
public class CalController {

    @Autowired
    private MapperFacade modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private ICalService iCalService;

    @RequestMapping("/{userUid}")
    public String getIcal(@PathVariable("userUid") String userUid) throws IllegalAccessException, IllegalArgumentException {
        UserAccount user;
        try {
            user = userRepository.findOne(UUID.fromString(userUid));
        } catch (IllegalArgumentException e) {
            throw new NotFound();
        }
        if (user == null) {
            throw new NotFound();
        }
        return iCalService.getCalendarForUser(user).toICal();
    }

}
