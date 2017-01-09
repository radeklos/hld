package com.caribou.company.repository;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Department;
import com.caribou.company.domain.Invitation;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class InvitationRepositoryTest extends IntegrationTests {

    @Autowired
    UserRepository userRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    InvitationRepository invitationRepository;

    Company company;

    Department department;

    @Before
    public void setUp() throws Exception {
        company = companyRepository.save(Factory.company());
        department = departmentRepository.save(Factory.department(company));
    }

    @Test
    public void keyIsNotEmpty() throws Exception {
        UserAccount userAccount = userRepository.save(Factory.userAccount());

        Invitation invitation = Invitation.newBuilder()
                .company(company)
                .department(department)
                .userAccount(userAccount)
                .build();
        invitation = invitationRepository.save(invitation);

        assertThat(invitation.getKey()).isNotEmpty();
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void userCannotHaveMoreInvitations() throws Exception {
        UserAccount userAccount = userRepository.save(Factory.userAccount());

        Invitation invitation = Invitation.newBuilder()
                .company(company)
                .department(department)
                .userAccount(userAccount)
                .build();
        invitationRepository.save(invitation);

        Company company = companyRepository.save(Factory.company());
        Department department = departmentRepository.save(Factory.department(company));
        invitation = Invitation.newBuilder()
                .company(company)
                .department(department)
                .userAccount(userAccount)
                .build();
        invitationRepository.save(invitation);
    }

}
