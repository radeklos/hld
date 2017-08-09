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

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.time.temporal.TemporalAdjusters.lastDayOfYear;


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

    public Observable<Boolean> performImport(List<EmployeeCsvParser.Row> rows, final Company company) {
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
                .flatMap(e -> Observable.just(userService.register(e.getMember())).flatMap(u -> departmentService.addEmployeeRx(e)));
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

    public void createEmployee(@NotNull UserAccount userAccount, @NotNull Department department, @NotNull LocalDate employmentStartDate) {
        userAccount.setPassword(UUID.randomUUID().toString());
        userService.create(userAccount);
        BigDecimal remainingAllowance = calculateRemainingAllowance(department, employmentStartDate);
        log.info("Calculated remaining allowance for number of days off {} for user={} who's starting on {} is {}",
                department.getDaysOff(), userAccount.getUid(), employmentStartDate, remainingAllowance);
        companyRepository.addEmployee(department, userAccount, Role.Viewer, remainingAllowance);
        sendInvitationEmail(userAccount, department);
    }

    private BigDecimal calculateRemainingAllowance(Department department, LocalDate start) {
        LocalDate lastDay = LocalDate.now().with(lastDayOfYear());
        BigDecimal daysOffPerMonth = department.getDaysOff().divide(BigDecimal.valueOf(12), 5, RoundingMode.HALF_UP);
        BigDecimal numberOfMonths = BigDecimal.valueOf(Math.max(1, Period.between(start, lastDay).getMonths()));
        return roundToNearestFive(daysOffPerMonth.multiply(numberOfMonths));
    }

    private void sendInvitationEmail(UserAccount userAccount, Department department) {
        Invitation invite = invitationBuilder(userAccount, department);
        Email email = Email.builder()
                .to(userAccount)
                .template(templateBuilder(invite))
                .build();
        invitationRepository.save(invite);
        emailSender.send(email, userAccount.getLocale());
    }

    private static BigDecimal roundToNearestFive(BigDecimal value) {
        BigDecimal increment = BigDecimal.valueOf(0.5);
        BigDecimal divided = value.divide(increment, 0, RoundingMode.HALF_DOWN);
        return divided.multiply(increment);
    }

    private static Invitation invitationBuilder(UserAccount userAccount, Department department) {
        return Invitation.builder()
                .key(UUID.randomUUID().toString())
                .department(department)
                .company(department.getCompany())
                .userAccount(userAccount).build();
    }

    public boolean sendInvitationEmail(DepartmentEmployee departmentEmployee) {
        sendInvitationEmail(departmentEmployee.getMember(), departmentEmployee.getDepartment());
        return true;
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
