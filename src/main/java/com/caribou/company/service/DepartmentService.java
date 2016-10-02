package com.caribou.company.service;

import com.caribou.company.domain.Department;
import com.caribou.company.repository.DepartmentRepository;
import org.springframework.stereotype.Service;


@Service
public class DepartmentService extends RxService.Imp<DepartmentRepository, Department, Long> {

}
