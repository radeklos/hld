package com.caribou.holiday.rest;

import com.caribou.auth.jwt.UserContext;
import com.caribou.auth.service.UserService;
import com.caribou.company.domain.CompanyEmployee;
import com.caribou.company.rest.ErrorHandler;
import com.caribou.company.service.CompanyService;
import com.caribou.company.service.NotFound;
import com.caribou.holiday.domain.Leave;
import com.caribou.holiday.rest.dto.LeaveDto;
import com.caribou.holiday.rest.dto.ListDto;
import com.caribou.holiday.service.LeaveService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import rx.Observable;
import rx.Single;

import javax.validation.Valid;


@RestController
@RequestMapping("/v1/users/{userUid}/leaves")
public class LeaveController {

    @Autowired
    private MapperFacade modelMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private LeaveService leaveService;

    @RequestMapping(method = RequestMethod.POST)
    public Single<ResponseEntity<LeaveDto>> create(@PathVariable("userUid") String userUid, @Valid @RequestBody LeaveDto leaveDto) {
        UserContext userDetails = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService
                .findByEmail(userDetails.getUsername())
                .filter(userAccount -> userAccount.getUid().toString().equals(userUid))
                .map(userAccount -> {
                    Leave leave = convert(leaveDto);
                    leave.setUserAccount(userAccount);
                    return leave;
                })
                .flatMap(leaveService::create)
                .map(d -> new ResponseEntity<>(convert(d), HttpStatus.CREATED))
                .onErrorReturn(ErrorHandler::h)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .toSingle();
    }

    private Leave convert(LeaveDto dto) {
        return modelMapper.map(dto, Leave.class);
    }

    private LeaveDto convert(Leave entity) {
        return modelMapper.map(entity, LeaveDto.class);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Single<ListDto<LeaveDto>> getList(@PathVariable("userUid") String userUid) {
        UserContext userDetails = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return companyService.getEmployeeByItsUid(userUid)
                .filter(e -> e.getCompany().getUid().equals(userDetails.getCompanyId()))
                .switchIfEmpty(Observable.error(new NotFound()))
                .map(CompanyEmployee::getMember)
                .flatMap(leaveService::findByUserAccount)
                .map(this::convert)
                .toList()
                .map(l -> ListDto.<LeaveDto>builder().items(l).build())
                .toSingle();
    }

    @RequestMapping(value = "/{leaveUid}", method = RequestMethod.POST)
    public ResponseEntity confirmLeave(@PathVariable("userUid") String userUid, @PathVariable("leaveUid") String leaveUid) {
        UserContext userDetails = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.accepted().build();
    }

    @RequestMapping(value = "/{leaveUid}", method = RequestMethod.DELETE)
    public ResponseEntity declineLeave(@PathVariable("userUid") String userUid, @PathVariable("leaveUid") String leaveUid) {
        UserContext userDetails = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.accepted().build();
    }
}
