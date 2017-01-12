package com.caribou.company.service;

import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Department;
import com.caribou.company.domain.DepartmentEmployee;
import com.caribou.company.domain.Role;
import com.caribou.company.repository.DepartmentRepository;
import com.caribou.company.service.parser.EmployeeCsvParser;
import com.caribou.email.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final DepartmentRepository departmentRepository;

    private final UserRepository userRepository;

    private final EmailSender emailSender;

    @Autowired
    public EmployeeService(DepartmentRepository departmentRepository, UserRepository userRepository, EmailSender emailSender) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.emailSender = emailSender;
    }

    public void importEmployee(List<EmployeeCsvParser.Row> row, Company compamy) {

    }

    DepartmentEmployee createDepartmentEmployee(EmployeeCsvParser.Row row, Company company) throws NotFound {
        UserAccount userAccount = UserAccount.newBuilder()
                .firstName(row.getFirstName())
                .lastName(row.getLastName())
                .email(row.getEmail())
                .build();

        Optional<Department> department = company.getDepartments().stream()
                .filter(d -> d.getName().equals(row.getDepartment()))
                .findFirst();
        if (!department.isPresent()) {
            throw new NotFound(String.format("Can't find department %s for company %s", row.getDepartment(), company.getName()));
        }
        DepartmentEmployee departmentEmployee = new DepartmentEmployee(department.get(), userAccount, Role.Viewer);
        return departmentEmployee;
    }

}
