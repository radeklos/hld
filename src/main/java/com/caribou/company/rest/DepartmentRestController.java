package com.caribou.company.rest;


import com.caribou.company.domain.Department;
import com.caribou.company.rest.dto.DepartmentDto;
import com.caribou.company.service.CompanyService;
import com.caribou.company.service.DepartmentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rx.Single;

import javax.validation.Valid;


@RestController
@RequestMapping("/v1/companies/{companyUid}/departments")
public class DepartmentRestController {

    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private CompanyService companyService;

    @Autowired
    private DepartmentService departmentService;

    @RequestMapping(value = "/{uid}", method = RequestMethod.GET)
    public Single<DepartmentDto> get(@PathVariable("companyUid") Long companyUid, @PathVariable("uid") Long uid) {
        return departmentService.get(uid)
                .filter(d -> d.getCompany().getUid().equals(companyUid))
                .map(this::convert).toSingle();
    }

    @RequestMapping(method = RequestMethod.PUT)
    public Single<DepartmentDto> create(@PathVariable("companyUid") Long companyUid, @Valid @RequestBody DepartmentDto departmentDto) {
        return companyService.get(companyUid)
                .flatMap(company -> {
                    Department entity = convert(departmentDto);
                    entity.setCompany(company);
                    return departmentService.create(entity);
                })
                .map(this::convert).toSingle();
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


    private Department convert(DepartmentDto dto) {
        return modelMapper.map(dto, Department.class);
    }

    private DepartmentDto convert(Department entity) {
        return modelMapper.map(entity, DepartmentDto.class);
    }

}
