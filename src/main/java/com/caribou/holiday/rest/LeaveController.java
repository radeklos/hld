package com.caribou.holiday.rest;

import com.caribou.auth.jwt.UserContext;
import com.caribou.auth.service.UserService;
import com.caribou.company.rest.ErrorHandler;
import com.caribou.holiday.domain.Leave;
import com.caribou.holiday.rest.dto.LeaveDto;
import com.caribou.holiday.service.LeaveService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import rx.Single;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/users/{userUid}/leaves")
public class LeaveController {

    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private UserService userService;

    @Autowired
    private LeaveService leaveService;

    @RequestMapping(method = RequestMethod.POST)
    public Single<ResponseEntity<LeaveDto>> create(@PathVariable("userUid") Long userUid, @Valid @RequestBody LeaveDto leaveDto) {
        UserContext userDetails = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService
                .findByEmail(userDetails.getUsername())
                .filter(userAccount -> userAccount.getUid().equals(userUid))
                .map(userAccount -> {
                    Leave leave = convert(leaveDto);
                    leave.setUserAccount(userAccount);
                    return leave;
                })
                .flatMap(leaveService::create)
                .map(d -> new ResponseEntity<>(convert(d), HttpStatus.CREATED))
                .onErrorReturn(ErrorHandler::h)
                .toSingle();
    }

    private Leave convert(LeaveDto dto) {
        return modelMapper.map(dto, Leave.class);
    }

    private LeaveDto convert(Leave entity) {
        return modelMapper.map(entity, LeaveDto.class);
    }

}
