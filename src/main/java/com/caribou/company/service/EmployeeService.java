package com.caribou.company.service;

import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.auth.service.UserService;
import com.caribou.company.domain.*;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.repository.InvitationRepository;
import com.caribou.company.service.parser.EmployeeCsvParser;
import com.caribou.email.Email;
import com.caribou.email.providers.EmailSender;
import com.caribou.email.templates.Invite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;
import rx.exceptions.Exceptions;

import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmployeeService {

    private final DepartmentService departmentRepository;
    private final UserService userService;
    private final EmailSender emailSender;

    private final Locale locale = Locale.UK;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    public EmployeeService(DepartmentService departmentRepository, UserService userService, EmailSender emailSender) {
        this.departmentRepository = departmentRepository;
        this.userService = userService;
        this.emailSender = emailSender;
    }

    public Observable<DepartmentEmployee> importEmployee(List<EmployeeCsvParser.Row> rows, final Company company) throws NotFound {
        return Observable.from(rows)
                .map(r -> {
                    try {
                        Company refreshedCompany = companyRepository.findOne(company.getUid());
                        return createDepartmentEmployee(r, refreshedCompany);
                    } catch (Throwable t) {
                        throw Exceptions.propagate(t);
                    }
                })
                .flatMap(e -> userService.create(e.getMember()).flatMap(u -> departmentRepository.addEmployee(e)));
    }

    DepartmentEmployee createDepartmentEmployee(EmployeeCsvParser.Row row, Company company) throws NotFound {
        Optional<UserAccount> alreadyExistingUser = userRepository.findByEmail(row.getEmail());
        UserAccount userAccount;
        userAccount = alreadyExistingUser.orElseGet(() -> UserAccount.newBuilder()
                .firstName(row.getFirstName())
                .lastName(row.getLastName())
                .email(row.getEmail())
                .password(UUID.randomUUID().toString())
                .build());

        Optional<Department> department = company.getDepartments().stream()
                .filter(d -> d.getName().equals(row.getDepartment()))
                .findFirst();
        if (!department.isPresent()) {
            throw new NotFound(String.format("Can't find department %s for company %s", row.getDepartment(), company.getName()), row.getDepartment());
        }
        return new DepartmentEmployee(department.get(), userAccount, BigDecimal.valueOf(row.getReamingHoliday()), Role.Viewer);
    }

    public void sendInvitationEmail(DepartmentEmployee departmentEmployee) {
        Invitation invite = invitationBuilder(departmentEmployee);

        Email email = Email.builder()
                .to(departmentEmployee.getMember())
                .template(templateBuilder(invite))
                .build();

        try {
            emailSender.send(email, locale);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private static Invitation invitationBuilder(DepartmentEmployee departmentEmployee) {
        return Invitation.newBuilder()
                .department(departmentEmployee.getDepartment())
                .company(departmentEmployee.getDepartment().getCompany())
                .userAccount(departmentEmployee.getMember()).build();
    }

    private static Invite templateBuilder(Invitation invitation) {
        return Invite.builder()
                .companyName(invitation.getDepartment().getCompany().getName())
                .departmentName(invitation.getDepartment().getName())
                .user(invitation.getUserAccount())
                .token(invitation.getKey()).build();
    }

}
