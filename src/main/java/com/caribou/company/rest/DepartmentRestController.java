package com.caribou.company.rest;


import com.caribou.auth.jwt.UserContext;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import rx.Observable;
import rx.Single;

import javax.validation.Valid;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


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
        UserContext userDetails = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return companyService.getByEmployeeEmail(companyUid, userDetails.getUsername())
                .flatMap(d -> Observable.create(subscriber -> {
                    d.getDepartments().forEach(subscriber::onNext);
                    subscriber.onCompleted();
                })).map(m -> this.convert((Department) m));
    }

    @RequestMapping(method = RequestMethod.POST)
    public Single<ResponseEntity<DepartmentDto>> create(@PathVariable("companyUid") Long companyUid, @Valid @RequestBody DepartmentDto departmentDto) {
        UserContext userDetails = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return companyService.getByEmployeeEmail(companyUid, userDetails.getUsername())
                .map(company1 -> {
                    for (CompanyEmployee f : company1.getEmployees()) {
                        if (f.getMember().getEmail().equals(userDetails.getUsername()) && f.getRole() == Role.Viewer) {
                            throw new AccessDeniedException("omg");
                        }
                    }
                    return company1;
                })
                .flatMap(company -> {
                    Department entity = convert(departmentDto);
                    entity.setCompany(company);
                    return departmentService.create(entity);
                })
                .map(d -> new ResponseEntity<>(convert(d), HttpStatus.CREATED))
                .onErrorReturn(ErrorHandler::h)
                .toSingle();
    }

    @RequestMapping(value = "/{uid}", method = RequestMethod.PUT)
    public Single<DepartmentDto> update(@PathVariable("companyUid") Long companyUid, @PathVariable("uid") Long uid, @Valid @RequestBody DepartmentDto departmentDto) {
        // TODO acl
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

    @RequestMapping(value = "/{departmentUid}/employees")
    public Observable<EmployeeDto> employee(@PathVariable("companyUid") Long companyUid, @PathVariable("departmentUid") Long departmentUid) {
        // TODO acl
        return departmentService.get(departmentUid)
                .flatMap(department -> Observable.create(subscriber -> {
                    department.getEmployees().forEach(subscriber::onNext);
                    subscriber.onCompleted();
                }))
                .map(entity -> modelMapper.map(entity, EmployeeDto.class));
    }

    private Department convert(DepartmentDto dto) {
        return modelMapper.map(dto, Department.class);
    }

    private DepartmentDto convert(Department entity) {
        DepartmentDto departmentDto = modelMapper.map(entity, DepartmentDto.class);
        departmentDto.add(linkTo(methodOn(DepartmentRestController.class).employee(entity.getCompany().getUid(), entity.getUid())).withRel("employees"));
        return departmentDto;
    }

}
