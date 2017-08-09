package com.caribou.holiday.service;

import com.caribou.auth.domain.UserAccount;
import com.caribou.company.domain.CompanyEmployee;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.service.NotFound;
import com.caribou.company.service.RxService;
import com.caribou.email.Email;
import com.caribou.email.providers.EmailSender;
import com.caribou.email.templates.LeaveApproved;
import com.caribou.holiday.domain.BankHoliday;
import com.caribou.holiday.domain.Leave;
import com.caribou.holiday.repository.BankHolidayRepository;
import com.caribou.holiday.repository.LeaveRepository;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class LeaveService extends RxService.Imp<LeaveRepository, Leave, UUID> {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private BankHolidayRepository bankHolidayRepository;

    @Autowired
    private EmailSender emailSender;

    public Observable<Leave> findByUserAccount(UserAccount userAccount) {
        return Observable.from(leaveRepository.findByUserAccount(userAccount));
    }

    @Override
    public Observable<Leave> create(Leave entity) {
        return super.create(createLeave(entity));
    }

    public List<EmployeeLeaves> getEmployeeLeaves(String companyId, final LocalDate from, final LocalDate to) {
        return companyRepository.findEmployeesByCompanyUid(UUID.fromString(companyId)).stream()
                .map(e -> EmployeeLeaves.builder()
                        .employee(e)
                        .leaves(leaveRepository.findByUserAccount(
                                e.getMember(),
                                Timestamp.valueOf(from.atStartOfDay()),
                                Timestamp.valueOf(to.atStartOfDay()))
                        )
                        .remaining(e.getRemainingAllowance().doubleValue())
                        .build()
                )
                .collect(Collectors.toList());
    }

    private Leave createLeave(Leave entity) {
        entity.setNumberOfDays(numberOfBookedDays(entity, BankHoliday.Country.CZ));
        entity.setApprover(findUserApprover(entity.getUserAccount()));
        return entity;
    }

    BigDecimal numberOfBookedDays(Leave leave, BankHoliday.Country country) {
        BigDecimal days = BigDecimal.ZERO;
        for (LocalDateTime day = leave.getStarting().toLocalDateTime();
             day.isBefore(leave.getEnding().toLocalDateTime());
             day = day.toLocalDate().atStartOfDay().plusDays(1)
                ) {
            LocalDate asLocalDate = day.toLocalDate();
            if (isWeekend(asLocalDate) || bankHolidayRepository.isBankHoliday(Date.valueOf(asLocalDate), country)) {
                continue;
            }
            LocalDateTime end = asLocalDate.atTime(LocalTime.MAX);
            if (leave.getEnding().toLocalDateTime().isBefore(end)) {
                end = leave.getEnding().toLocalDateTime();
            }
            Duration duration = Duration.between(day, end);
            days = days.add(BigDecimal.valueOf(duration.toHours()).divide(BigDecimal.valueOf(24), 1, BigDecimal.ROUND_HALF_UP));
        }
        return days;
    }

    /**
     * Find user approver if any
     *
     * @param userAccount
     * @return
     */
    UserAccount findUserApprover(UserAccount userAccount) {
        Optional<CompanyEmployee> employee = companyRepository.findEmployeeByUserAccount(userAccount);
        if (!employee.isPresent()) {
            throw new NotFound();
        }
        if (employee.get().getApprover() != null) {
            return employee.get().getApprover();
        }
        return employee.get().getDepartment().getBoss();
    }

    public void approve(Leave leave) {
        Optional<CompanyEmployee> employee0 = companyRepository.findEmployeeByUserAccount(leave.getUserAccount());
        if (employee0.isPresent()) {
            CompanyEmployee employee = employee0.get();
            companyRepository.updateRemainingAllowance(employee, employee.getRemainingAllowance().subtract(leave.getNumberOfDays()));
            leave.setStatus(Leave.Status.APPROVED);
            leaveRepository.save(leave);
            sentEmail(leave);
        }
    }

    private void sentEmail(Leave leave) {
        Email email = Email.builder()
                .to(leave.getUserAccount())
                .template(new LeaveApproved(leave))
                .build();
        emailSender.send(email, leave.getUserAccount().getLocale());
    }

    private boolean isWeekend(LocalDate localDate) {
        Set<DayOfWeek> weekend = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
        return weekend.contains(localDate.getDayOfWeek());
    }

    @Data
    @Builder
    public static class EmployeeLeaves {
        private final CompanyEmployee employee;
        private final List<Leave> leaves;
        private final double remaining;
    }

}
