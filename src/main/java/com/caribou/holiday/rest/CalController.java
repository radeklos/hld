package com.caribou.holiday.rest;

import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.company.service.CompanyService;
import com.caribou.company.service.NotFound;
import com.caribou.holiday.service.ICalService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> getIcal(@PathVariable("userUid") String userUid) throws IllegalAccessException, IllegalArgumentException {
        UserAccount user;
        try {
            user = userRepository.findOne(UUID.fromString(userUid));
        } catch (IllegalArgumentException e) {
            throw new NotFound();
        }
        if (user == null) {
            throw new NotFound();
        }
        HttpHeaders bla = new HttpHeaders();
        bla.set("Content-Type", "text/calendar");
        bla.set("Content-Disposition", "attachment;filename=" + slugify(user) + ".ics");
        return ResponseEntity.ok().headers(bla).body(iCalService.getCalendarForUser(user).toICal());
    }

    private static String slugify(UserAccount user) {
        StringBuilder builder = new StringBuilder();
        builder.append(user.getFirstName()).append(" ").append(user.getLastName()).append(" ").append(user.getUid());
        return builder.toString().trim().toLowerCase().replaceAll("[^A-Za-z0-9]", "-");
    }

}
