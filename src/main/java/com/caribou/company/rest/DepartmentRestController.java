package com.caribou.company.rest;


import com.caribou.auth.service.UserService;
import com.caribou.company.domain.CompanyEmployee;
import com.caribou.company.domain.Department;
import com.caribou.company.domain.Role;
import com.caribou.company.rest.dto.DepartmentDto;
import com.caribou.company.rest.dto.EmployeeDto;
import com.caribou.company.service.CompanyService;
import com.caribou.company.service.DepartmentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import rx.Observable;
import rx.Single;

import javax.validation.Valid;
import java.util.Iterator;


@RestController
@RequestMapping("/v1/companies/{companyUid}/departments")
public class DepartmentRestController {

    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private CompanyService companyService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/{uid}", method = RequestMethod.GET)
    public Single<DepartmentDto> get(@PathVariable("companyUid") Long companyUid, @PathVariable("uid") Long uid) {
        return departmentService.get(uid)
                .filter(d -> d.getCompany().getUid().equals(companyUid))
                .map(this::convert).toSingle();
    }

    @RequestMapping(method = RequestMethod.GET)
    public Observable<DepartmentDto> getList(@PathVariable("companyUid") Long companyUid) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return companyService.getForEmployeeEmail(companyUid, userDetails.getUsername())
                .flatMap(d -> Observable.create(subscriber -> {
                    d.getDepartments().forEach(subscriber::onNext);
                    subscriber.onCompleted();
                })).map(m -> this.convert((Department) m));
    }

    @RequestMapping(method = RequestMethod.PUT)
    public Single<ResponseEntity<DepartmentDto>> create(@PathVariable("companyUid") Long companyUid, @Valid @RequestBody DepartmentDto departmentDto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final HttpStatus[] status = {HttpStatus.OK};

        return companyService.getForEmployeeEmail(companyUid, userDetails.getUsername())
                .map(company1 -> {
                    for (Iterator<CompanyEmployee> iterator = company1.getEmployees().iterator(); iterator.hasNext(); ) {
                        CompanyEmployee f = iterator.next();
                        if (f.getMember().getEmail().equals(userDetails.getUsername()) && f.getRole() == Role.Viewer) {
                            status[0] = HttpStatus.FORBIDDEN;
                        }
                    }
                    return company1;
                })
                .flatMap(company -> {
                    Department entity = convert(departmentDto);
                    entity.setCompany(company);
                    return departmentService.create(entity);
                })
                .map(d -> new ResponseEntity<DepartmentDto>(status[0]))
                .toSingle();
    }

    @RequestMapping(value = "/{uid}", method = RequestMethod.POST)
    public Single<DepartmentDto> update(@PathVariable("companyUid") Long companyUid, @PathVariable("uid") Long uid, @Valid @RequestBody DepartmentDto departmentDto) {
        return companyService.get(companyUid)
                .flatMap(company -> {
                    Department entity = convert(departmentDto);
                    entity.setCompany(company);
                    return departmentService.update(uid, entity);
                })
                .map(d -> {
                    modelMapper.map(d, departmentDto);
                    return departmentDto;
                }).toSingle();
    }

    @RequestMapping(value = "/{uid}/employee")
    public Observable<EmployeeDto> employee(@PathVariable("companyUid") Long companyUid, @PathVariable("uid") Long uid) {
        return departmentService.get(companyUid)
                .filter(d -> d.getCompany().getUid().equals(companyUid))
                .map(d -> new EmployeeDto());
    }

    private Department convert(DepartmentDto dto) {
        return modelMapper.map(dto, Department.class);
    }

    private DepartmentDto convert(Department entity) {
        return modelMapper.map(entity, DepartmentDto.class);
    }

}
