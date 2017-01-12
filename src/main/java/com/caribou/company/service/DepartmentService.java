package com.caribou.company.service;

import com.caribou.auth.domain.UserAccount;
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

    public Observable<DepartmentEmployee> addEmployee(Department department, UserAccount user, Role role) {
        return Observable.create(subscriber -> {
            try {
                DepartmentEmployee departmentEmployee = department.addEmployee(user, role);
                department.getCompany().addEmployee(user, role);
                companyRepository.save(department.getCompany());
                subscriber.onNext(departmentEmployee);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
