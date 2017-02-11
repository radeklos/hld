package com.caribou.company.service;

import com.caribou.company.domain.Department;
import com.caribou.company.domain.DepartmentEmployee;
import com.caribou.company.domain.Role;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;


@Service
public class DepartmentService extends RxService.Imp<DepartmentRepository, Department, Long> {

    @Autowired
    CompanyRepository companyRepository;

    public Observable<DepartmentEmployee> addEmployee(DepartmentEmployee departmentEmployee) {
        return Observable.create(subscriber -> {
            try {
                if (repository.getEmployee(departmentEmployee.getDepartment(), departmentEmployee.getMember()).isPresent()) {
                    repository.updateEmployee(departmentEmployee.getMember(), departmentEmployee.getRole());
                } else {
                    repository.addEmployee(departmentEmployee.getDepartment(), departmentEmployee.getMember(), departmentEmployee.getRemainingDaysOff(), departmentEmployee.getRole());
                    companyRepository.addEmployee(departmentEmployee.getDepartment().getCompany(), departmentEmployee.getMember(), Role.Viewer);
                }
                subscriber.onNext(repository.getEmployee(departmentEmployee.getDepartment(), departmentEmployee.getMember()).get());
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
