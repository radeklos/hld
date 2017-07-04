package com.caribou.holiday.rest;

import com.caribou.auth.jwt.UserContext;
import com.caribou.auth.rest.dto.UserAccountDto;
import com.caribou.holiday.domain.Leave;
import com.caribou.holiday.rest.dto.EmployeeLeavesDto;
import com.caribou.holiday.rest.dto.LeaveDto;
import com.caribou.holiday.rest.dto.ListDto;
import com.caribou.holiday.service.LeaveService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/v1/company/{companyId}/leaves")
public class CompanyLeaveController {

    @Autowired
    private MapperFacade modelMapper;

    @Autowired
    private LeaveService leaveService;

    @RequestMapping(method = RequestMethod.GET)
    public ListDto<EmployeeLeavesDto> getList(@PathVariable("companyId") String companyId,
                                              @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                              @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        UserContext userDetails = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<LeaveService.EmployeeLeaves> leaves = leaveService.getEmployeeLeaves(companyId, from, to);
        List<EmployeeLeavesDto> bla = leaves.stream().map(this::map).collect(Collectors.toList());
        return ListDto.<EmployeeLeavesDto>builder()
                .items(bla)
                .total(bla.size())
                .build();
    }

    private EmployeeLeavesDto map(LeaveService.EmployeeLeaves employeeLeaves) {
        return EmployeeLeavesDto.builder()
                .employee(modelMapper.map(employeeLeaves.getUserAccount(), UserAccountDto.class))
                .leaves(map(employeeLeaves.getLeaves(), LeaveDto.class))
                .build();
    }

    private List<LeaveDto> map(List<Leave> leaves, Class<LeaveDto> userAccountDtoClass) {
        return leaves.stream()
                .map(l -> modelMapper.map(l, userAccountDtoClass))
                .collect(Collectors.toList());
    }


}
