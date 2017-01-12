package com.caribou.company.service;

import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.service.UserService;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Department;
import com.caribou.company.domain.DepartmentEmployee;
import com.caribou.company.domain.Role;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.service.parser.EmployeeCsvParser;
import com.caribou.email.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;
import rx.exceptions.Exceptions;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmployeeService {

    private final DepartmentService departmentRepository;
    private final UserService userService;
    private final EmailSender emailSender;
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    public EmployeeService(DepartmentService departmentRepository, UserService userService, EmailSender emailSender) {
        this.departmentRepository = departmentRepository;
        this.userService = userService;
        this.emailSender = emailSender;
    }

    public void importEmployee(List<EmployeeCsvParser.Row> row, final Company compamy) {
        Observable<DepartmentEmployee> source = Observable.from(row)
                .map(r -> {
                    try {
                        Company refreshedCompany = companyRepository.findOne(compamy.getUid());
                        return createDepartmentEmployee(r, refreshedCompany);
                    } catch (Throwable t) {
                        throw Exceptions.propagate(t);
                    }
                })
                .flatMap(e -> userService.create(e.getMember()).flatMap(u -> departmentRepository.addEmployee(e.getDepartment(), u, Role.Viewer)));
        source.toBlocking().forEach(u -> System.out.println(u.getMember().getEmail()));
    }

    DepartmentEmployee createDepartmentEmployee(EmployeeCsvParser.Row row, Company company) throws NotFound {
        UserAccount userAccount = UserAccount.newBuilder()
                .firstName(row.getFirstName())
                .lastName(row.getLastName())
                .email(row.getEmail())
                .password(UUID.randomUUID().toString())
                .build();

        Optional<Department> department = company.getDepartments().stream()
                .filter(d -> d.getName().equals(row.getDepartment()))
                .findFirst();
        if (!department.isPresent()) {
            throw new NotFound(String.format("Can't find department %s for company %s", row.getDepartment(), company.getName()));
        }
        return new DepartmentEmployee(department.get(), userAccount, Role.Viewer);
    }

}
