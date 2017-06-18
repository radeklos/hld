package com.caribou.holiday.rest;

import com.caribou.auth.service.UserService;
import com.caribou.company.service.CompanyService;
import com.caribou.holiday.service.LeaveService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;


@RestController
@RequestMapping("/v1/company/{companyId}/leaves")
public class CompanyLeaveController {

    private static final ZoneId UTC = ZoneId.of("UTC");

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private LeaveService leaveService;

//    @RequestMapping(method = RequestMethod.GET)
//    public Single<ListDto<LeaveDto>> getList(@PathVariable("companyId") String companyId) {
//        UserContext userDetails = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        return companyService.get(companyId)
//                .filter(e -> e.getCompany().getUid().equals(userDetails.getCompanyId()))
//                .switchIfEmpty(Observable.error(new NotFound()))
//                .map(CompanyEmployee::getMember)
//                .flatMap(leaveService::findByUserAccount)
//                .map(this::convert)
//                .toList()
//                .map(l -> ListDto.<LeaveDto>builder().items(l).build())
//                .toSingle();
//    }

}
