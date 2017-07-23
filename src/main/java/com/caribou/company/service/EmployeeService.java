package com.caribou.company.service;

import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.auth.service.UserService;
import com.caribou.company.Pair;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Department;
import com.caribou.company.domain.DepartmentEmployee;
import com.caribou.company.domain.Invitation;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.repository.DepartmentRepository;
import com.caribou.company.repository.InvitationRepository;
import com.caribou.company.service.parser.EmployeeCsvParser;
import com.caribou.email.Email;
import com.caribou.email.providers.EmailSender;
import com.caribou.email.templates.Invite;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;
import rx.exceptions.Exceptions;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
public class EmployeeService {

    private final DepartmentRepository departmentRepository;

    private final DepartmentService departmentService;

    private final UserService userService;

    private final EmailSender emailSender;

    private final CompanyRepository companyRepository;

    private final UserRepository userRepository;

    private final InvitationRepository invitationRepository;

    private final Locale locale = Locale.UK;  // TODO should be dynamic based on company

    @Autowired
    public EmployeeService(DepartmentRepository departmentRepository, DepartmentService departmentService, UserService userService, EmailSender emailSender, InvitationRepository invitationRepository, UserRepository userRepository, CompanyRepository companyRepository) {
        this.departmentRepository = departmentRepository;
        this.departmentService = departmentService;
        this.userService = userService;
        this.emailSender = emailSender;
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    public Observable<Pair<Boolean, Invitation>> performImport(List<EmployeeCsvParser.Row> rows, final Company company) {
        return importEmployee(rows, company).map(this::sendInvitationEmail);
    }

    public Observable<DepartmentEmployee> importEmployee(List<EmployeeCsvParser.Row> rows, final Company company) {
        return Observable.from(rows)
                .map(r -> {
                    try {
                        Company refreshedCompany = companyRepository.findOne(company.getUid());
                        return createDepartmentEmployee(r, refreshedCompany);
                    } catch (Exception t) {
                        throw Exceptions.propagate(t);
                    }
                })
                .flatMap(e -> userService.create(e.getMember()).flatMap(u -> departmentService.addEmployeeRx(e)));
    }

    DepartmentEmployee createDepartmentEmployee(EmployeeCsvParser.Row row, Company company) throws DepartmentNotFound {
        Optional<UserAccount> alreadyExistingUser = userRepository.findByEmail(row.getEmail());
        UserAccount userAccount;
        userAccount = alreadyExistingUser.orElseGet(() -> UserAccount.newBuilder()
                .firstName(row.getFirstName())
                .lastName(row.getLastName())
                .email(row.getEmail())
                .password(UUID.randomUUID().toString())
                .build());
        Optional<Department> departmentOpt = company.getDepartments().stream()
                .filter(d -> d.getName().equals(row.getDepartment()))
                .findFirst();
        Department department = departmentOpt.orElseGet(() -> departmentRepository.save(
                Department.builder()
                        .company(company)
                        .name(row.getDepartment())
                        .daysOff(company.getDefaultDaysOff())
                        .build()
        ));
        return new DepartmentEmployee(department, userAccount);
    }

    public Pair<Boolean, Invitation> sendInvitationEmail(DepartmentEmployee departmentEmployee) {
        boolean successful = true;
        Invitation invite = invitationBuilder(departmentEmployee);
        Email email = Email.builder()
                .to(departmentEmployee.getMember())
                .template(templateBuilder(invite))
                .build();
        try {
            invitationRepository.save(invite);
            emailSender.send(email, locale);
        } catch (MessagingException e) {
            log.error("Can not send invitation to user={} of department={}", departmentEmployee.getMember().getEmail(), departmentEmployee.getDepartment().getUid());
            successful = false;
        }
        return new Pair<>(successful, invite);
    }

    private static Invitation invitationBuilder(DepartmentEmployee departmentEmployee) {
        return Invitation.builder()
                .key(UUID.randomUUID().toString())
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

    public class DepartmentNotFound extends NotFound {
        private DepartmentNotFound(String message) {
            super(message);
        }
    }
}
