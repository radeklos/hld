package com.caribou.company.service;

import com.caribou.auth.repository.UserRepository;
import com.caribou.company.repository.DepartmentRepository;
import com.caribou.email.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
