package com.caribou.company.service;

import com.caribou.company.domain.Department;
import com.caribou.company.domain.DepartmentEmployee;
import com.caribou.company.domain.Role;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.util.Optional;


@Service
public class DepartmentService extends RxService.Imp<DepartmentRepository, Department, String> {

    private final CompanyRepository companyRepository;

    @Autowired
    public DepartmentService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Observable<DepartmentEmployee> addEmployeeRx(DepartmentEmployee departmentEmployee) {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(addEmployee(departmentEmployee));
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public DepartmentEmployee addEmployee(DepartmentEmployee departmentEmployee) throws NotFound {
        if (repository.getEmployee(departmentEmployee.getDepartment(), departmentEmployee.getMember()).isPresent()) {
            repository.updateEmployee(departmentEmployee.getMember(), departmentEmployee.getRole());
        } else {
            repository.addEmployee(departmentEmployee.getDepartment(), departmentEmployee.getMember(), departmentEmployee.getRemainingDaysOff(), departmentEmployee.getRole());
            companyRepository.addEmployee(departmentEmployee.getDepartment().getCompany(), departmentEmployee.getMember(), Role.Viewer);
        }
        Optional<DepartmentEmployee> de = repository.getEmployee(departmentEmployee.getDepartment(), departmentEmployee.getMember());
        if (de.isPresent()) {
            return de.get();
        }
        throw new NotFound("Can't find department employee");
    }
}
