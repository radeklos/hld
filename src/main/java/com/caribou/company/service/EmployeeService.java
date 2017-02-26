package com.caribou.company.service;

import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.auth.service.UserService;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Department;
import com.caribou.company.domain.DepartmentEmployee;
import com.caribou.company.domain.Invitation;
import com.caribou.company.domain.Role;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.repository.InvitationRepository;
import com.caribou.company.service.parser.EmployeeCsvParser;
import com.caribou.email.Email;
import com.caribou.email.providers.EmailSender;
import com.caribou.email.templates.Invite;
import com.sun.tools.javac.util.Pair;
import lombok.extern.slf4j.Slf4j;
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


@Slf4j
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

    public Observable<Pair<Boolean, Invitation>> performImport(List<EmployeeCsvParser.Row> rows, final Company company) throws NotFound {
        return importEmployee(rows, company).map(this::sendInvitationEmail);
    }

    Observable<DepartmentEmployee> importEmployee(List<EmployeeCsvParser.Row> rows, final Company company) throws NotFound {
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

    private Pair<Boolean, Invitation> sendInvitationEmail(DepartmentEmployee departmentEmployee) {
        Boolean successful;
        Invitation invite = invitationBuilder(departmentEmployee);
        Email email = Email.builder()
                .to(departmentEmployee.getMember())
                .template(templateBuilder(invite))
                .build();

        try {
            invitationRepository.save(invite);
            emailSender.send(email, locale);
            successful = true;
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

}
